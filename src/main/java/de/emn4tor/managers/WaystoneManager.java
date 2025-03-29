package de.emn4tor.managers;

/*
 *  @author: Emn4tor
 *  @created: 29.03.2025
 */

import de.emn4tor.WaystonePlugin;
import de.emn4tor.database.DatabaseManager;
import de.emn4tor.models.Waystone;
import de.emn4tor.utils.SoundUtils;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.HashMap;
import java.util.Map;


public class WaystoneManager {

    private final WaystonePlugin plugin;
    private final DatabaseManager databaseManager;
    private final Map<UUID, Long> cooldowns = new HashMap<>();

    public WaystoneManager(WaystonePlugin plugin) {
        this.plugin = plugin;
        this.databaseManager = plugin.getDatabaseManager();
    }

    public void createWaystone(String name, Location location, Player player) {
        Waystone waystone = new Waystone(name, location, player.getUniqueId());
        databaseManager.saveWaystone(waystone);

        // Play effects
        SoundUtils.playWaystoneCreateSound(location);
        SoundUtils.spawnWaystoneCreateParticles(location);

        player.sendMessage("§6Waystone §e" + name + " §6created!");
    }

    public void deleteWaystone(int id) {
        databaseManager.deleteWaystone(id);
    }

    public List<Waystone> getPlayerWaystones(UUID playerUUID) {
        return databaseManager.getPlayerWaystones(playerUUID);
    }

    public Optional<Waystone> getWaystone(int id) {
        return databaseManager.getWaystone(id);
    }

    public Optional<Waystone> getWaystoneAtLocation(Location location) {
        return databaseManager.getWaystoneAtLocation(location);
    }

    public void discoverWaystone(Player player, int waystoneId) {
        databaseManager.addDiscoveredWaystone(player.getUniqueId(), waystoneId);

        getWaystone(waystoneId).ifPresent(waystone -> {
            player.sendMessage("§6You discovered waystone §e" + waystone.getName() + "§6!");
            SoundUtils.playWaystoneDiscoverSound(player.getLocation());
        });
    }

    public void teleportToWaystone(Player player, int waystoneId) {
        // Check if player is on cooldown
        if (isOnCooldown(player.getUniqueId())) {
            int remainingSeconds = getRemainingCooldown(player.getUniqueId());
            player.sendMessage("§cYou must wait §e" + remainingSeconds + " seconds §cbefore teleporting again!");
            return;
        }

        getWaystone(waystoneId).ifPresent(waystone -> {
            // Play departure effects
            SoundUtils.playWaystoneTeleportSound(player.getLocation());
            SoundUtils.spawnWaystoneTeleportParticles(player.getLocation());

            // Teleport player
            player.teleport(waystone.getLocation().add(0.5, 1, 0.5), PlayerTeleportEvent.TeleportCause.PLUGIN);

            // Play arrival effects
            SoundUtils.playWaystoneTeleportSound(player.getLocation());
            SoundUtils.spawnWaystoneTeleportParticles(player.getLocation());

            // Set cooldown
            setCooldown(player.getUniqueId());

            player.sendMessage("§6Teleported to §e" + waystone.getName() + "§6!");
        });
    }

    /**
     * Check if a player is on cooldown
     */
    private boolean isOnCooldown(UUID playerUUID) {
        if (!cooldowns.containsKey(playerUUID)) {
            return false;
        }

        // Get the cooldown time from config
        int cooldownSeconds = plugin.getConfig().getInt("waystones.teleport-cooldown", 60);

        // Get the time when the cooldown expires
        long cooldownExpiration = cooldowns.get(playerUUID) + (cooldownSeconds * 1000L);

        // Check if the cooldown has expired
        if (System.currentTimeMillis() >= cooldownExpiration) {
            cooldowns.remove(playerUUID);
            return false;
        }

        return true;
    }

    /**
     * Get the remaining cooldown time in seconds
     */
    private int getRemainingCooldown(UUID playerUUID) {
        if (!cooldowns.containsKey(playerUUID)) {
            return 0;
        }

        // Get the cooldown time from config
        int cooldownSeconds = plugin.getConfig().getInt("waystones.teleport-cooldown", 60);

        // Get the time when the cooldown expires
        long cooldownExpiration = cooldowns.get(playerUUID) + (cooldownSeconds * 1000L);

        // Calculate remaining time
        long remainingMillis = cooldownExpiration - System.currentTimeMillis();

        if (remainingMillis <= 0) {
            cooldowns.remove(playerUUID);
            return 0;
        }

        return (int) (remainingMillis / 1000);
    }

    /**
     * Set a player on cooldown
     */
    private void setCooldown(UUID playerUUID) {
        cooldowns.put(playerUUID, System.currentTimeMillis());
    }
}




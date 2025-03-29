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

public class WaystoneManager {

    private final WaystonePlugin plugin;
    private final DatabaseManager databaseManager;

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
        getWaystone(waystoneId).ifPresent(waystone -> {
            // Play departure effects
            SoundUtils.playWaystoneTeleportSound(player.getLocation());
            SoundUtils.spawnWaystoneTeleportParticles(player.getLocation());

            // Teleport player
            player.teleport(waystone.getLocation().add(0.5, 1, 0.5), PlayerTeleportEvent.TeleportCause.PLUGIN);

            // Play arrival effects
            SoundUtils.playWaystoneTeleportSound(player.getLocation());
            SoundUtils.spawnWaystoneTeleportParticles(player.getLocation());

            player.sendMessage("§6Teleported to §e" + waystone.getName() + "§6!");
        });
    }
}


package de.emn4tor.listeners;

/*
 *  @author: Emn4tor
 *  @created: 29.03.2025
 */

import de.emn4tor.WaystonePlugin;
import de.emn4tor.WaystoneRecipe;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class PlayerJoinListener implements Listener {

    private final WaystonePlugin plugin;

    public PlayerJoinListener(WaystonePlugin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            int firstJoinWaystones = plugin.getConfig().getInt("waystones.first-join-waystones", 0);

            if (firstJoinWaystones > 0) {
                ItemStack waystoneItem = WaystoneRecipe.createWaystoneItem(plugin);
                waystoneItem.setAmount(firstJoinWaystones);

                if (player.getInventory().firstEmpty() != -1) {
                    player.getInventory().addItem(waystoneItem);
                    player.sendMessage("§6Welcome! You've received §e" + firstJoinWaystones +
                            (firstJoinWaystones == 1 ? " §6waystone!" : " §6waystones!"));
                } else {
                    player.getWorld().dropItem(player.getLocation(), waystoneItem);
                    player.sendMessage("§6Welcome! You've received §e" + firstJoinWaystones +
                            (firstJoinWaystones == 1 ? " §6waystone" : " §6waystones") +
                            "§6, but your inventory was full so it was dropped on the ground!");
                }
            }
        }
    }
}

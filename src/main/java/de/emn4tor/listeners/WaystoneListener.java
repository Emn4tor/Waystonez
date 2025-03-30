package de.emn4tor.listeners;

/*
 *  @author: Emn4tor
 *  @created: 29.03.2025
 */

import de.emn4tor.WaystonePlugin;
import de.emn4tor.gui.AnvilGUI;
import de.emn4tor.gui.WaystoneGUI;
import de.emn4tor.managers.WaystoneManager;
import de.emn4tor.models.Waystone;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class WaystoneListener implements Listener {

    private final WaystonePlugin plugin;
    private final WaystoneManager waystoneManager;

    public WaystoneListener(WaystonePlugin plugin) {
        this.plugin = plugin;
        this.waystoneManager = plugin.getWaystoneManager();
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        Block block = event.getBlock();
        ItemStack item = event.getItemInHand();

        // if waystone
        if (block.getType() == Material.LODESTONE && isWaystoneItem(item)) {
            // Open anvil GUI
            plugin.getServer().getScheduler().runTaskLater(plugin, () -> {
                new AnvilGUI(plugin, player, "Waystone", (p, name) -> {
                    // Create waystone with given name
                    waystoneManager.createWaystone(name, block.getLocation(), player);
                    return AnvilGUI.Response.close();
                }).open();
            }, 1L);
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        // if broken block is waystone
        if (block.getType() == Material.LODESTONE) {
            Optional<Waystone> optionalWaystone = waystoneManager.getWaystoneAtLocation(block.getLocation());

            if (optionalWaystone.isPresent()) {
                Waystone waystone = optionalWaystone.get();
                Player player = event.getPlayer();

                // Only allow the owner to break waystone
                if (!player.getUniqueId().equals(waystone.getOwnerUUID()) && !player.hasPermission("waystones.admin")) {
                    event.setCancelled(true);
                    player.sendMessage("§cYou cannot break someone else's waystone!");
                    return;
                }

                // Delete the waystone from the database
                waystoneManager.deleteWaystone(waystone.getId());
                player.sendMessage("§6Waystone §e" + waystone.getName() + " §6destroyed!");


                event.setDropItems(false);

                // Drop the waystone item
                ItemStack waystoneItem = new ItemStack(Material.LODESTONE);
                ItemMeta meta = waystoneItem.getItemMeta();
                meta.setDisplayName("§6Waystone");

                List<String> lore = new ArrayList<>();
                lore.add("§7Place to create a waypoint");
                lore.add("§7Right-click to teleport between waypoints");
                meta.setLore(lore);

                // Add custom tag
                meta.getPersistentDataContainer().set(new NamespacedKey(plugin, "waystone"), PersistentDataType.BYTE, (byte) 1);

                waystoneItem.setItemMeta(meta);

                block.getWorld().dropItemNaturally(block.getLocation(), waystoneItem);
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK || event.getHand() != EquipmentSlot.HAND) {
            return;
        }

        Block block = event.getClickedBlock();
        if (block == null) return;

        // Check if the block is waystone
        if (block.getType() == Material.LODESTONE) {
            Optional<Waystone> optionalWaystone = waystoneManager.getWaystoneAtLocation(block.getLocation());

            if (optionalWaystone.isPresent()) {
                event.setCancelled(true);

                Waystone waystone = optionalWaystone.get();
                Player player = event.getPlayer();

                // Add waystone to player's discovered waystones
                waystoneManager.discoverWaystone(player, waystone.getId());

                // Open the waystone GUI
                WaystoneGUI.open(player);
            }
        }
    }

    private boolean isWaystoneItem(ItemStack item) {
        if (item == null || !item.hasItemMeta()) {
            return false;
        }

        ItemMeta meta = item.getItemMeta();
        return meta.getPersistentDataContainer().has(plugin.getWaystoneKey(), PersistentDataType.BYTE);
    }
}

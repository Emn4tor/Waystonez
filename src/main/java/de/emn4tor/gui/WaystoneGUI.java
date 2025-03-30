package de.emn4tor.gui;

/*
 *  @author: Emn4tor
 *  @created: 29.03.2025
 */

import de.emn4tor.WaystonePlugin;
import de.emn4tor.managers.WaystoneManager;
import de.emn4tor.models.Waystone;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class WaystoneGUI implements Listener {

    private final WaystonePlugin plugin;
    private final UUID playerUUID;
    private final WaystoneManager waystoneManager;
    private Inventory inventory;

    public WaystoneGUI(WaystonePlugin plugin, UUID playerUUID) {
        this.plugin = plugin;
        this.playerUUID = playerUUID;
        this.waystoneManager = plugin.getWaystoneManager();

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void open() {
        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) {
            HandlerList.unregisterAll(this);
            return;
        }

        List<Waystone> waystones = waystoneManager.getPlayerWaystones(playerUUID);
        int size = Math.min(54, ((waystones.size() + 8) / 9) * 9); // Round up to nearest multiple of 9

        inventory = Bukkit.createInventory(player, size, "Your Waystones");

        for (int i = 0; i < waystones.size(); i++) {
            Waystone waystone = waystones.get(i);

            ItemStack item = new ItemStack(Material.LODESTONE);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName("§6" + waystone.getName());

            List<String> lore = new ArrayList<>();
            lore.add("§7Location: §f" + waystone.getLocationString());
            lore.add("§7Click to teleport");
            meta.setLore(lore);

            // Store the waystone ID in the item's NBT
            meta.getPersistentDataContainer().set(
                    plugin.getWaystoneKey(),
                    org.bukkit.persistence.PersistentDataType.INTEGER,
                    waystone.getId()
            );

            item.setItemMeta(meta);
            inventory.setItem(i, item);
        }

        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() != inventory) return;
        event.setCancelled(true);

        if (event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR) return;

        ItemStack clickedItem = event.getCurrentItem();
        ItemMeta meta = clickedItem.getItemMeta();
        if (meta == null) return;

        Player player = Bukkit.getPlayer(playerUUID);
        if (player == null) {
            HandlerList.unregisterAll(this);
            return;
        }

        // Get the waystone ID from the item's NBT
        if (meta.getPersistentDataContainer().has(plugin.getWaystoneKey(), org.bukkit.persistence.PersistentDataType.INTEGER)) {
            int waystoneId = meta.getPersistentDataContainer().get(
                    plugin.getWaystoneKey(),
                    org.bukkit.persistence.PersistentDataType.INTEGER
            );

            player.closeInventory();
            waystoneManager.teleportToWaystone(player, waystoneId);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory() == inventory) {
            HandlerList.unregisterAll(this);
        }
    }
}

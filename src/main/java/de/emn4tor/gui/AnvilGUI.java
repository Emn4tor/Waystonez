package de.emn4tor.gui;

/*
 *  @author: Emn4tor
 *  @created: 29.03.2025
 */

import de.emn4tor.WaystonePlugin;
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

import java.util.function.BiFunction;

public class AnvilGUI implements Listener {

    private final WaystonePlugin plugin;
    private final Player player;
    private final String title;
    private final BiFunction<Player, String, Response> completeFunction;
    private Inventory inventory;

    public AnvilGUI(WaystonePlugin plugin, Player player, String title, BiFunction<Player, String, Response> completeFunction) {
        this.plugin = plugin;
        this.player = player;
        this.title = title;
        this.completeFunction = completeFunction;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    public void open() {
        // Create fake anvil inv
        inventory = Bukkit.createInventory(player, 9, "Name your Waystone");

        // Create input item
        ItemStack nameItem = new ItemStack(Material.PAPER);
        ItemMeta meta = nameItem.getItemMeta();
        meta.setDisplayName(title);
        nameItem.setItemMeta(meta);

        // Create the confirm button
        ItemStack confirmItem = new ItemStack(Material.LIME_WOOL);
        ItemMeta confirmMeta = confirmItem.getItemMeta();
        confirmMeta.setDisplayName("§aConfirm");
        confirmItem.setItemMeta(confirmMeta);

        inventory.setItem(0, nameItem);
        inventory.setItem(8, confirmItem);


        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getInventory() != inventory) return;

        event.setCancelled(true);

        if (event.getRawSlot() == 8) {
            // confirm button
            ItemStack nameItem = inventory.getItem(0);
            if (nameItem != null && nameItem.hasItemMeta() && nameItem.getItemMeta().hasDisplayName()) {
                String name = nameItem.getItemMeta().getDisplayName();
                Response response = completeFunction.apply(player, name);

                if (response.closeInventory()) {
                    player.closeInventory();
                }
            }
        } else if (event.getRawSlot() == 0) {
            // Allow renaming paper
            player.sendMessage("§6Type the name for your waystone in chat:");
            player.closeInventory();

            // Register a chat listener
            Bukkit.getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onChat(org.bukkit.event.player.AsyncPlayerChatEvent chatEvent) {
                    if (chatEvent.getPlayer() == player) {
                        chatEvent.setCancelled(true);
                        String name = chatEvent.getMessage();

                        // Apply the name and call the complete function
                        Response response = completeFunction.apply(player, name);

                        // Unregister this listener
                        HandlerList.unregisterAll(this);
                    }
                }
            }, plugin);
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (event.getInventory() == inventory) {
            // Unregister this listener when the inventory is closed
            HandlerList.unregisterAll(this);
        }
    }

    public static class Response {
        private final boolean closeInventory;

        private Response(boolean closeInventory) {
            this.closeInventory = closeInventory;
        }

        public boolean closeInventory() {
            return closeInventory;
        }

        public static Response close() {
            return new Response(true);
        }

        public static Response keepOpen() {
            return new Response(false);
        }
    }
}


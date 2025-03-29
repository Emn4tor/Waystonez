package de.emn4tor;

/*
 *  @author: Emn4tor
 *  @created: 29.03.2025
 */

import org.bukkit.NamespacedKey;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;

public class WaystoneRecipe {

    public static void register(WaystonePlugin plugin, NamespacedKey key) {
        // Create item
        ItemStack waystoneItem = new ItemStack(Material.LODESTONE);
        ItemMeta meta = waystoneItem.getItemMeta();
        meta.setDisplayName("§6Waystone");

        List<String> lore = new ArrayList<>();
        lore.add("§7Place to create a waypoint");
        lore.add("§7Right-click to teleport between waypoints");
        meta.setLore(lore);

        // Add custom tag to identify this as waystone
        meta.getPersistentDataContainer().set(key, PersistentDataType.BYTE, (byte) 1);

        waystoneItem.setItemMeta(meta);

        ShapedRecipe recipe = new ShapedRecipe(key, waystoneItem);

        recipe.shape(
                "DSN",
                "SPS",
                "GSE");
        recipe.setIngredient('S', Material.STONE_BRICKS);
        recipe.setIngredient('D', Material.DIAMOND);
        recipe.
        recipe.setIngredient('P', Material.ENDER_PEARL);

        // Register recipe
        plugin.getServer().addRecipe(recipe);
    }

    public static ItemStack createWaystoneItem(WaystonePlugin plugin) {
        ItemStack waystoneItem = new ItemStack(Material.LODESTONE);
        ItemMeta meta = waystoneItem.getItemMeta();
        meta.setDisplayName("§6Waystone");

        List<String> lore = new ArrayList<>();
        lore.add("§7Place to create a waypoint");
        lore.add("§7Right-click to teleport between waypoints");
        meta.setLore(lore);

        // Add custom tag to identify as waystone
        meta.getPersistentDataContainer().set(plugin.getWaystoneKey(), PersistentDataType.BYTE, (byte) 1);

        waystoneItem.setItemMeta(meta);
        return waystoneItem;
    }
}


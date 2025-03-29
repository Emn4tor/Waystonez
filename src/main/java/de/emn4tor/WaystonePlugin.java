package de.emn4tor;

import de.emn4tor.commands.WaystoneCommands;
import de.emn4tor.database.DatabaseManager;
import de.emn4tor.listeners.PlayerJoinListener;
import de.emn4tor.listeners.WaystoneListener;
import de.emn4tor.managers.WaystoneManager;
import org.bukkit.Bukkit;
import org.bukkit.NamespacedKey;
import org.bukkit.plugin.java.JavaPlugin;

public class WaystonePlugin extends JavaPlugin {

    private static WaystonePlugin instance;
    private DatabaseManager databaseManager;
    private WaystoneManager waystoneManager;
    private NamespacedKey waystoneKey;

    @Override
    public void onEnable() {
        instance = this;

        // Create config
        saveDefaultConfig();

        // Initialize database
        this.databaseManager = new DatabaseManager(this);
        this.databaseManager.initialize();

        // waystone manager
        this.waystoneManager = new WaystoneManager(this);

        //recipe
        this.waystoneKey = new NamespacedKey(this, "waystone");
        WaystoneRecipe.register(this, waystoneKey);

        //events
        Bukkit.getPluginManager().registerEvents(new WaystoneListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerJoinListener(this), this);


        //commands
        getCommand("waystone").setExecutor(new WaystoneCommands(this));

        getLogger().info("WaystonePlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        // Close database conn
        if (databaseManager != null) {
            databaseManager.close();
        }

        getLogger().info("WaystonePlugin has been disabled!");
    }

    public static WaystonePlugin getInstance() {
        return instance;
    }

    public DatabaseManager getDatabaseManager() {
        return databaseManager;
    }

    public WaystoneManager getWaystoneManager() {
        return waystoneManager;
    }

    public NamespacedKey getWaystoneKey() {
        return waystoneKey;
    }
}

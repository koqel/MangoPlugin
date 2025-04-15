package services.plasma.mangoPlugin;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import services.plasma.mangoPlugin.commands.MangoCommands;
import services.plasma.mangoPlugin.events.MangoEvents;
import services.plasma.mangoPlugin.items.MangoItems;
import services.plasma.mangoPlugin.utils.ConfigUtils;

public class MangoPlugin extends JavaPlugin {

    private static MangoPlugin instance;
    private ConfigUtils configUtils;
    private MangoItems mangoItems;

    @Override
    public void onEnable() {
        instance = this;

        saveDefaultConfig();
        configUtils = new ConfigUtils(this);

        mangoItems = new MangoItems(this);
        mangoItems.registerItems();

        Bukkit.getPluginManager().registerEvents(new MangoEvents(this), this);

        getCommand("mango").setExecutor(new MangoCommands(this));

        getLogger().info("MangoPlugin has been enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("MangoPlugin has been disabled!");
    }

    /**
     * Get the plugin instance.
     * @return The plugin instance
     */
    public static MangoPlugin getInstance() {
        return instance;
    }

    /**
     * Get the configuration utilities.
     * @return The configuration utilities
     */
    public ConfigUtils getConfigUtils() {
        return configUtils;
    }

    /**
     * Get the mango items manager.
     * @return The mango items manager
     */
    public MangoItems getMangoItems() {
        return mangoItems;
    }
}
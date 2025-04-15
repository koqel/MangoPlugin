package services.plasma.mangoPlugin.utils;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import services.plasma.mangoPlugin.MangoPlugin;

import java.util.List;
import java.util.stream.Collectors;

public class ConfigUtils {

    private final MangoPlugin plugin;
    private final FileConfiguration config;

    public ConfigUtils(MangoPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    /**
     * Reload the configuration.
     */
    public void reloadConfig() {
        plugin.reloadConfig();
    }

    /**
     * Get the mango item name with color codes.
     * @return The formatted mango name
     */
    public String getMangoName() {
        return ChatColor.translateAlternateColorCodes('&', config.getString("items.mango.name", "&6Mango"));
    }

    /**
     * Get the mango item lore with color codes.
     * @return The formatted mango lore
     */
    public List<String> getMangoLore() {
        return config.getStringList("items.mango.lore").stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());
    }

    /**
     * Get the hunger points restored by eating a mango.
     * @return The hunger points
     */
    public int getMangoFoodLevel() {
        return config.getInt("items.mango.food_level", 4);
    }

    /**
     * Get the saturation restored by eating a mango.
     * @return The saturation
     */
    public float getMangoSaturation() {
        return (float) config.getDouble("items.mango.saturation", 4.0);
    }

    /**
     * Get the mango seed item name with color codes.
     * @return The formatted mango seed name
     */
    public String getMangoSeedName() {
        return ChatColor.translateAlternateColorCodes('&', config.getString("items.mango_seed.name", "&eMango Seed"));
    }

    /**
     * Get the mango seed item lore with color codes.
     * @return The formatted mango seed lore
     */
    public List<String> getMangoSeedLore() {
        return config.getStringList("items.mango_seed.lore").stream()
                .map(line -> ChatColor.translateAlternateColorCodes('&', line))
                .collect(Collectors.toList());
    }

    /**
     * Get the chance for a mango to drop a seed when eaten.
     * @return The seed drop chance (0.0 to 1.0)
     */
    public double getSeedDropChance() {
        return config.getDouble("mechanics.seed_drop_chance", 0.3);
    }

    /**
     * Get the chance for a leaf to drop a mango when broken.
     * @return The mango drop chance (0.0 to 1.0)
     */
    public double getLeafDropChance() {
        return config.getDouble("mechanics.leaf_drop_chance", 0.1);
    }

    /**
     * Get the growth stages duration in minutes.
     * @return The growth time in minutes
     */
    public int getGrowthTime() {
        return config.getInt("mechanics.growth_time", 30);
    }

    /**
     * Check if using custom tree generation.
     * @return True if using custom tree generation
     */
    public boolean useCustomTreeGeneration() {
        return config.getBoolean("mechanics.use_custom_tree_generation", true);
    }
}
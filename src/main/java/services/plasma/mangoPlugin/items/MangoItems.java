package services.plasma.mangoPlugin.items;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapelessRecipe;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;
import services.plasma.mangoPlugin.MangoPlugin;

import java.util.Optional;

public class MangoItems {

    private final MangoPlugin plugin;
    private final NamespacedKey mangoKey;
    private final NamespacedKey mangoSeedKey;

    private ItemStack mangoItem;
    private ItemStack mangoSeedItem;

    public MangoItems(MangoPlugin plugin) {
        this.plugin = plugin;
        this.mangoKey = new NamespacedKey(plugin, "mango");
        this.mangoSeedKey = new NamespacedKey(plugin, "mango_seed");

        createMangoItem();
        createMangoSeedItem();
    }

    /**
     * Register all custom items and recipes.
     */
    public void registerItems() {
        registerMangoRecipes();
    }

    /**
     * Create the mango item.
     */
    private void createMangoItem() {
        Optional<XMaterial> material = XMaterial.APPLE.isSupported() ?
                Optional.of(XMaterial.APPLE) : Optional.of(XMaterial.GOLDEN_APPLE);

        mangoItem = material.get().parseItem();
        ItemMeta meta = mangoItem.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(plugin.getConfigUtils().getMangoName());
            meta.setLore(plugin.getConfigUtils().getMangoLore());
            meta.setCustomModelData(1001);

            meta.getPersistentDataContainer().set(mangoKey, PersistentDataType.INTEGER, 1);

            meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
            mangoItem.setItemMeta(meta);
        }
    }

    /**
     * Create the mango seed item.
     */
    private void createMangoSeedItem() {
        Optional<XMaterial> material = XMaterial.WHEAT_SEEDS.isSupported() ?
                Optional.of(XMaterial.WHEAT_SEEDS) : Optional.of(XMaterial.COCOA_BEANS);

        mangoSeedItem = material.get().parseItem();
        ItemMeta meta = mangoSeedItem.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(plugin.getConfigUtils().getMangoSeedName());
            meta.setLore(plugin.getConfigUtils().getMangoSeedLore());
            meta.setCustomModelData(1002);

            meta.getPersistentDataContainer().set(mangoSeedKey, PersistentDataType.INTEGER, 1);
            mangoSeedItem.setItemMeta(meta);
        }
    }

    /**
     * Register recipes for mango items.
     */
    private void registerMangoRecipes() {
        ShapelessRecipe mangoRecipe = new ShapelessRecipe(mangoKey, getMangoItem());

        Optional<XMaterial> appleMaterial = XMaterial.APPLE.isSupported() ?
                Optional.of(XMaterial.APPLE) : Optional.of(XMaterial.GOLDEN_APPLE);

        Optional<XMaterial> dyeMaterial = XMaterial.YELLOW_DYE.isSupported() ?
                Optional.of(XMaterial.YELLOW_DYE) : Optional.of(XMaterial.DANDELION);

        mangoRecipe.addIngredient(1, appleMaterial.get().parseMaterial());
        mangoRecipe.addIngredient(3, dyeMaterial.get().parseMaterial());

        plugin.getServer().addRecipe(mangoRecipe);
    }

    /**
     * Get a new mango item.
     * @return A copy of the mango item
     */
    public ItemStack getMangoItem() {
        return mangoItem.clone();
    }

    /**
     * Get a new mango seed item.
     * @return A copy of the mango seed item
     */
    public ItemStack getMangoSeedItem() {
        return mangoSeedItem.clone();
    }

    /**
     * Check if an item is a mango.
     * @param item The item to check
     * @return True if the item is a mango
     */
    public boolean isMango(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;

        return item.getItemMeta().getPersistentDataContainer()
                .has(mangoKey, PersistentDataType.INTEGER);
    }

    /**
     * Check if an item is a mango seed.
     * @param item The item to check
     * @return True if the item is a mango seed
     */
    public boolean isMangoSeed(ItemStack item) {
        if (item == null || !item.hasItemMeta()) return false;

        return item.getItemMeta().getPersistentDataContainer()
                .has(mangoSeedKey, PersistentDataType.INTEGER);
    }
}
package services.plasma.mangoPlugin.events;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;
import services.plasma.mangoPlugin.MangoPlugin;

import java.util.*;

public class MangoEvents implements Listener {

    private final MangoPlugin plugin;
    private final Random random;
    private final Map<Location, Integer> growingTrees;

    public MangoEvents(MangoPlugin plugin) {
        this.plugin = plugin;
        this.random = new Random();
        this.growingTrees = new HashMap<>();
    }

    /**
     * Handle mango consumption
     */
    @EventHandler
    public void onPlayerConsume(PlayerItemConsumeEvent event) {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (plugin.getMangoItems().isMango(item)) {
            player.setFoodLevel(Math.min(player.getFoodLevel() + plugin.getConfigUtils().getMangoFoodLevel(), 20));
            player.setSaturation(Math.min(player.getSaturation() + plugin.getConfigUtils().getMangoSaturation(), 20f));

            player.getWorld().spawnParticle(Particle.ITEM_CRACK,
                    player.getLocation().add(0, 1.5, 0),
                    10, 0.3, 0.3, 0.3, 0.05,
                    XMaterial.YELLOW_DYE.parseItem());

            if (random.nextDouble() <= plugin.getConfigUtils().getSeedDropChance()) {
                player.getWorld().dropItemNaturally(player.getLocation(),
                        plugin.getMangoItems().getMangoSeedItem());
            }
        }
    }

    /**
     * Handle mango seed planting
     */
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK ||
                event.getHand() != EquipmentSlot.HAND ||
                event.getClickedBlock() == null) {
            return;
        }

        Player player = event.getPlayer();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        Block clickedBlock = event.getClickedBlock();

        if (plugin.getMangoItems().isMangoSeed(itemInHand)) {
            Material blockType = clickedBlock.getType();

            if (XMaterial.matchXMaterial(blockType) == XMaterial.DIRT ||
                    XMaterial.matchXMaterial(blockType) == XMaterial.GRASS_BLOCK ||
                    XMaterial.matchXMaterial(blockType) == XMaterial.FARMLAND) {

                Block aboveBlock = clickedBlock.getRelative(BlockFace.UP);

                if (aboveBlock.getType() == Material.AIR) {
                    event.setCancelled(true);

                    if (itemInHand.getAmount() > 1) {
                        itemInHand.setAmount(itemInHand.getAmount() - 1);
                    } else {
                        player.getInventory().setItemInMainHand(null);
                    }

                    Optional<XMaterial> sapling = XMaterial.JUNGLE_SAPLING.isSupported() ?
                            Optional.of(XMaterial.JUNGLE_SAPLING) :
                            Optional.of(XMaterial.OAK_SAPLING);

                    aboveBlock.setType(sapling.get().parseMaterial());

                    startTreeGrowth(aboveBlock.getLocation());

                    player.sendMessage("§6You've planted a mango seed!");
                }
            }
        }
    }

    /**
     * Handle leaf breaking for mango drops
     */
    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();

        // Check if breaking a leaf block
        if (XMaterial.matchXMaterial(block.getType()).name().contains("LEAVES")) {
            // Chance to drop a mango
            if (random.nextDouble() <= plugin.getConfigUtils().getLeafDropChance()) {
                block.getWorld().dropItemNaturally(block.getLocation(),
                        plugin.getMangoItems().getMangoItem());
            }
        }
    }

    /**
     * Start the growth process for a mango tree
     * @param location The location of the sapling
     */
    private void startTreeGrowth(Location location) {
        int growthTimeMinutes = plugin.getConfigUtils().getGrowthTime();
        int growthTimeTicks = growthTimeMinutes * 1200;

        // Store the tree location for tracking
        growingTrees.put(location, 0);

        // Define growth stages
        final int maxStages = 3;

        // Start growth process
        new BukkitRunnable() {
            int stage = 0;
            final Location treeLoc = location.clone();

            @Override
            public void run() {
                // Check if the sapling is still there
                Block saplingBlock = treeLoc.getBlock();
                if (!XMaterial.matchXMaterial(saplingBlock.getType()).name().contains("SAPLING")) {
                    growingTrees.remove(treeLoc);
                    this.cancel();
                    return;
                }

                stage++;
                growingTrees.put(treeLoc, stage);

                treeLoc.getWorld().spawnParticle(Particle.VILLAGER_HAPPY,
                        treeLoc.clone().add(0.5, 0.5, 0.5),
                        10, 0.3, 0.5, 0.3, 0.05);

                if (stage >= maxStages) {
                    growingTrees.remove(treeLoc);
                    growMangoTree(treeLoc);
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, growthTimeTicks / maxStages, growthTimeTicks / maxStages);
    }

    /**
     * Generate a mango tree at the given location
     * @param location The location to grow the tree
     */
    private void growMangoTree(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        location.getBlock().setType(Material.AIR);

        if (plugin.getConfigUtils().useCustomTreeGeneration()) {
            generateCustomMangoTree(location);
        } else {
            world.generateTree(location, org.bukkit.TreeType.JUNGLE);
        }
    }

    /**
     * Generate a custom mango tree structure
     * @param location The base location
     */
    private void generateCustomMangoTree(Location location) {
        World world = location.getWorld();
        if (world == null) return;

        // Tree height (5-7 blocks)
        int height = 5 + random.nextInt(3);

        // Generate trunk
        for (int y = 0; y < height; y++) {
            Block block = world.getBlockAt(location.getBlockX(), location.getBlockY() + y, location.getBlockZ());
            block.setType(Material.valueOf(XMaterial.JUNGLE_LOG.name()));
        }

        for (int x = -2; x <= 2; x++) {
            for (int z = -2; z <= 2; z++) {
                for (int y = height - 3; y <= height + 1; y++) {
                    if ((Math.abs(x) == 2 && Math.abs(z) == 2) && random.nextBoolean()) {
                        continue;
                    }

                    if (y == height + 1 && (Math.abs(x) == 2 || Math.abs(z) == 2)) {
                        continue;
                    }

                    Block block = world.getBlockAt(
                            location.getBlockX() + x,
                            location.getBlockY() + y,
                            location.getBlockZ() + z
                    );

                    if (x == 0 && z == 0 && y < height) {
                        continue;
                    }

                    block.setType(Material.valueOf(XMaterial.JUNGLE_LEAVES.name()));
                }
            }
        }

        world.spawnParticle(Particle.VILLAGER_HAPPY,
                location.clone().add(0.5, height / 2.0, 0.5),
                30, 1.0, height / 2.0, 1.0, 0.05);
    }
}
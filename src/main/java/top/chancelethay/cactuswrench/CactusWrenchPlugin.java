package top.chancelethay.cactuswrench;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

public class CactusWrenchPlugin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        getLogger().info("CactusWrench loading...");
        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("CactusWrench is enabled!");
    }

    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        if (itemInMainHand.getType() == Material.CACTUS && event.hasBlock()) {
            Block block = event.getClickedBlock();

            if (block != null) {
                if (block.getType().isInteractable()) {
                    if (player.isSneaking()) {
                        rotateBlock(block);
                    }
                } else {
                    rotateBlock(block);
                }
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInOffHand = player.getInventory().getItemInOffHand();

        if (itemInOffHand.getType() == Material.CACTUS) {
            Block block = event.getBlockPlaced();

            if (block.getBlockData() instanceof Directional) {
                rotateBlock(block);
            }
        }
    }

    private void rotateBlock(Block block) {
        if (block.getBlockData() instanceof Directional) {
            Directional directional = (Directional) block.getBlockData();

            org.bukkit.block.BlockFace currentFacing = directional.getFacing();
            org.bukkit.block.BlockFace newFacing = null;

            switch (currentFacing) {
                case NORTH:
                    newFacing = org.bukkit.block.BlockFace.SOUTH;
                    break;
                case SOUTH:
                    newFacing = org.bukkit.block.BlockFace.NORTH;
                    break;
                case EAST:
                    newFacing = org.bukkit.block.BlockFace.WEST;
                    break;
                case WEST:
                    newFacing = org.bukkit.block.BlockFace.EAST;
                    break;
                case DOWN:
                    newFacing = org.bukkit.block.BlockFace.UP;
                    break;
                case UP:
                    newFacing = org.bukkit.block.BlockFace.DOWN;
                    break;
                default:
                    break;
            }

            if (newFacing != null && currentFacing != newFacing) {
                directional.setFacing(newFacing);
                block.setBlockData(directional, true);
            }
        }
    }
}

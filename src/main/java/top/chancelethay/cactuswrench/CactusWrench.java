package top.chancelethay.cactuswrench;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.HashMap;
import java.util.UUID;

public class CactusWrench extends JavaPlugin implements Listener {

    private FileConfiguration messagesConfig; // 用于存储语言文件内容

    private final HashMap<UUID, Long> cooldowns = new HashMap<>();
    private long cooldownMillis;
    private boolean doRotatedBlockTick;
    private boolean doFlippedBlockTick;


    @Override
    public void onEnable() {
        getLogger().info("CactusWrench loading...");

        loadConfig();
        loadMessages();

        getServer().getPluginManager().registerEvents(this, this);
        getLogger().info("CactusWrench is enabled!");
    }


    public void loadConfig() {
        saveDefaultConfig();
        this.cooldownMillis = getConfig().getLong("cooldown-milliseconds");
        this.doRotatedBlockTick = getConfig().getBoolean("do-rotated-block-tick");
        this.doFlippedBlockTick = getConfig().getBoolean("do-flipped-block-tick");
    }


    public void loadMessages() {
        String lang = getConfig().getString("language", "en_us");
        File langFile = new File(getDataFolder(), "language/" + lang + ".yml");

        if (!langFile.exists()) {
            langFile.getParentFile().mkdirs();
            saveResource("language/" + lang + ".yml", false);
        }

        messagesConfig = YamlConfiguration.loadConfiguration(langFile);
        getLogger().info("Loaded language: " + lang);
    }


    public String getMessage(String path) {
        String message = messagesConfig.getString(path, "&cMessage not found: " + path);
        return ChatColor.translateAlternateColorCodes('&', message);
    }


    @EventHandler
    public void onPlayerRightClick(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack itemInMainHand = player.getInventory().getItemInMainHand();

        if (itemInMainHand.getType() == Material.CACTUS && event.hasBlock()) {

            if (this.cooldownMillis > 0) {
                if (cooldowns.containsKey(player.getUniqueId())) {
                    long timeElapsed = System.currentTimeMillis() - cooldowns.get(player.getUniqueId());
                    if (timeElapsed < this.cooldownMillis) {
                        long remainingMillis = this.cooldownMillis - timeElapsed;
                        double remainingSeconds = remainingMillis / 1000.0;
                        String formattedTime = String.format("%.1f", remainingSeconds);
                        String messageTemplate = getMessage("cooldown-message");
                        String finalMessage = messageTemplate.replace("{time}", formattedTime);
                        player.sendMessage(finalMessage);
                        return;
                    }
                }
            }

        }

        if (itemInMainHand.getType() == Material.CACTUS && event.hasBlock()) {
            Block block = event.getClickedBlock();

            if (block != null) {
                if (block.getBlockData() instanceof Directional){
                    if (LegitManager.legitCheck(block)) {
                        if (block.getType().isInteractable()) {
                            if (player.isSneaking()) {
                                rotateBlock(block);
                                cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
                            }
                        } else {
                            rotateBlock(block);
                            cooldowns.put(player.getUniqueId(), System.currentTimeMillis());
                        }
                    }
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
                if (LegitManager.legitCheck(block)) {
                    flipBlock(block);
                }
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
                    newFacing = BlockFace.EAST;
                    break;
                case EAST:
                    newFacing = BlockFace.SOUTH;
                    break;
                case SOUTH:
                    newFacing = BlockFace.WEST;
                    break;
                case WEST:
                    newFacing = BlockFace.NORTH;
                    break;
                case DOWN:
                    newFacing = BlockFace.UP;
                    break;
                case UP:
                    newFacing = BlockFace.DOWN;
                    break;
                default:
                    break;
            }

            if (newFacing != null && currentFacing != newFacing) {
                directional.setFacing(newFacing);
                block.setBlockData(directional, true);
                if (this.doRotatedBlockTick) {
                    block.tick();
                }
            }
        }
    }


    private void flipBlock(Block block) {
        if (block.getBlockData() instanceof Directional) {
            Directional directional = (Directional) block.getBlockData();

            org.bukkit.block.BlockFace currentFacing = directional.getFacing();
            org.bukkit.block.BlockFace newFacing = null;

            switch (currentFacing) {
                case NORTH:
                    newFacing = BlockFace.SOUTH;
                    break;
                case EAST:
                    newFacing = BlockFace.WEST;
                    break;
                case SOUTH:
                    newFacing = BlockFace.NORTH;
                    break;
                case WEST:
                    newFacing = BlockFace.EAST;
                    break;
                case DOWN:
                    newFacing = BlockFace.UP;
                    break;
                case UP:
                    newFacing = BlockFace.DOWN;
                    break;
                default:
                    break;
            }

            if (newFacing != null && currentFacing != newFacing) {
                directional.setFacing(newFacing);
                block.setBlockData(directional, true);
                if (doFlippedBlockTick) {
                    block.tick();
                }
            }
        }
    }
}

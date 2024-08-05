package com.multiplayer.doorlocker;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.type.Door;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockRedstoneEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

public class DoorEventListener implements Listener {
    private final DoorLocker plugin;

    public DoorEventListener(DoorLocker plugin) {
        this.plugin = plugin;

        new BukkitRunnable() {
            @Override
            public void run() {
                loadDoorData();
            }
        }.runTaskLater(plugin, 20L);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getAction() == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();
            if (block != null && block.getType() == Material.IRON_DOOR) {
                Block bottomBlock = getBottomBlock(block);
                if (bottomBlock.hasMetadata("doorKey")) {
                    Player player = event.getPlayer();
                    ItemStack item = player.getInventory().getItemInMainHand();
                    MetadataValue metadata = bottomBlock.getMetadata("doorKey").get(0);
                    String key = metadata.asString();
                    if (item != null && item.hasItemMeta() && item.getItemMeta().getDisplayName().equals(key)) {
                        openDoorTemporarily(bottomBlock);
                        event.setCancelled(true);
                    } else {
                        ItemStack itemInHand = player.getInventory().getItemInMainHand();
                        if (itemInHand == null || itemInHand.getType() == Material.AIR) event.setCancelled(true);
                        else {
                            sendTitle(player, vars.key_denied);
                            event.setCancelled(true);
                        }
                    }
                }
            }
        } else if (event.getAction() == Action.PHYSICAL) {
            Block block = event.getClickedBlock();
            if (block != null && block.getType() == Material.IRON_DOOR) {
                Block bottomBlock = getBottomBlock(block);
                if (bottomBlock.hasMetadata("doorKey")) {
                    event.setCancelled(true);
                }
            }
        }
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        Block block = event.getBlock();
        Block bottomBlock = getBottomBlock(block);
        if (block.getType() == Material.IRON_DOOR || bottomBlock.getType() == Material.IRON_DOOR) {
            Block targetBlock = block.getType() == Material.IRON_DOOR ? block : bottomBlock;
            if (block.hasMetadata("doorKey") || bottomBlock.hasMetadata("doorKey")) {
                event.setCancelled(true);
                sendTitle(event.getPlayer(), vars.you_cant_knock_door);
            }
        }
        // Запретить ломать блок под заблокированной дверью
        Block blockAbove = block.getRelative(BlockFace.UP);
        if (blockAbove.getType() == Material.IRON_DOOR) {
            Block bottomDoorBlock = getBottomBlock(blockAbove);
            if (bottomDoorBlock.hasMetadata("doorKey")) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onBlockRedstone(BlockRedstoneEvent event) {
        Block block = event.getBlock();
        for (BlockFace face : BlockFace.values()) {
            Block adjacentBlock = block.getRelative(face);
            if (adjacentBlock.getType() == Material.IRON_DOOR) {
                Block bottomBlock = getBottomBlock(adjacentBlock);
                if (bottomBlock.hasMetadata("doorKey")) {
                    event.setNewCurrent(0);
                    return;
                }
            }
        }
    }

    private Block getBottomBlock(Block block) {
        if (block.getType() == Material.IRON_DOOR && block.getBlockData() instanceof Door door) {
            return door.getHalf() == Door.Half.TOP ? block.getRelative(BlockFace.DOWN) : block;
        }
        return block;
    }

    private void openDoorTemporarily(Block block) {
        if (block.getBlockData() instanceof Door door) {
            door.setOpen(true);
            block.setBlockData(door);

            Block topBlock = block.getRelative(BlockFace.UP);
            if (topBlock.getBlockData() instanceof Door topDoor) {
                topDoor.setOpen(true);
                topBlock.setBlockData(topDoor);

                block.getWorld().playSound(block.getLocation(), "minecraft:block.iron_door.open", 1.0f, 1.0f);

                plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
                    door.setOpen(false);
                    block.setBlockData(door);

                    topDoor.setOpen(false);
                    topBlock.setBlockData(topDoor);

                    block.getWorld().playSound(block.getLocation(), "minecraft:block.iron_door.close", 1.0f, 1.0f);
                }, 30L);
            }
        }
    }

    private void sendTitle(Player player, String message) {
        player.sendActionBar(message);
    }

    private void loadDoorData() {
        FileConfiguration config = plugin.getConfig();
        if (config.contains("doors")) {
            for (String location : config.getConfigurationSection("doors").getKeys(false)) {
                String[] locParts = location.split(",");
                if (locParts.length == 4) {
                    String worldName = locParts[0];
                    int x = Integer.parseInt(locParts[1]);
                    int y = Integer.parseInt(locParts[2]);
                    int z = Integer.parseInt(locParts[3]);

                    Block block = Bukkit.getWorld(worldName).getBlockAt(x, y, z);
                    String key = config.getString("doors." + location + ".key");
                    String owner = config.getString("doors." + location + ".owner");

                    if (key != null && owner != null) {
                        block.setMetadata("doorKey", new FixedMetadataValue(plugin, key));
                        block.setMetadata("doorOwner", new FixedMetadataValue(plugin, owner));
                    }
                }
            }
        }
    }
}

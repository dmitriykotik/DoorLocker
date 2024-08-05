package com.multiplayer.doorlocker;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.UUID;

public class Commands implements CommandExecutor {

    private final DoorLocker plugin;

    public Commands(DoorLocker plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            if (args[0].equals("help")) {
                sender.sendMessage(" §7-+- §aDoorLocker §7-+-");
                sender.sendMessage(" §7- §adoorlocker help §f- §7" + vars.help_help);
                sender.sendMessage(" §7- §adoorlocker set <key> §f- §7" + vars.help_set);
                sender.sendMessage(" §7- §adoorlocker delete §f- §7" + vars.help_delete);
                if (sender.hasPermission("doorlocker.admin")){
                    sender.sendMessage(" §7- §adoorlocker destroy <nick> §f- §7" + vars.help_destroy);
                    sender.sendMessage(" §7- §adoorlocker info <key> §f- §7" + vars.help_info);
                    sender.sendMessage(" §7- §adoorlocker reload §f- §7" + vars.help_reload);
                }
            } else if (args[0].equals("reload")) {
                if (sender.hasPermission("doorlocker.admin")) {
                    sender.sendMessage(vars.prefix + " " + vars.reload_start);
                    plugin.reloadPLG();
                    sender.sendMessage(vars.prefix + " " + vars.reload_done);
                } else {
                    sender.sendMessage(vars.prefix + " " + vars.access_denied);
                }
            } else if (args[0].equals("delete")) {
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    Block block = player.getTargetBlockExact(5);
                    if (block != null && block.getType() == Material.IRON_DOOR) {
                        Block bottomBlock = getBottomBlock(block);
                        if (bottomBlock.hasMetadata("doorKey") && bottomBlock.hasMetadata("doorOwner")) {
                            UUID ownerUUID = UUID.fromString(bottomBlock.getMetadata("doorOwner").get(0).asString());
                            if (player.getUniqueId().equals(ownerUUID)) {
                                bottomBlock.removeMetadata("doorKey", plugin);
                                bottomBlock.removeMetadata("doorOwner", plugin);
                                removeDoorData(bottomBlock);
                                sendTitle(player, vars.lock_deleted);
                            } else {
                                sendTitle(player, vars.you_not_owner);
                            }
                        } else {
                            sendTitle(player, vars.door_doesnt_have_lock);
                        }
                    } else {
                        sendTitle(player, vars.unlock_door);
                    }
                } else {
                    sender.sendMessage(vars.prefix + " " + vars.only_players);
                }
            } else if (args[0].equals("set")) {
                if (args.length != 2) {
                    sender.sendMessage(vars.prefix + " " + vars.more_args);
                    return true;
                }
                if (sender instanceof Player) {
                    Player player = (Player) sender;
                    String key = args[1];
                    Block block = player.getTargetBlockExact(5);
                    ItemStack itemInHand = player.getInventory().getItemInMainHand();
                    if (itemInHand == null || itemInHand.getType() == Material.AIR) {
                        sendTitle(player, vars.take_any_object);
                        return true;
                    }

                    if (block != null && block.getType() == Material.IRON_DOOR) {
                        Block bottomBlock = getBottomBlock(block);
                        if (bottomBlock.hasMetadata("doorKey")) {
                            sendTitle(player, vars.door_locked);
                            return true;
                        }
                        bottomBlock.setMetadata("doorKey", new FixedMetadataValue(plugin, key));
                        bottomBlock.setMetadata("doorOwner", new FixedMetadataValue(plugin, player.getUniqueId().toString()));
                        saveDoorData(bottomBlock, key, player.getUniqueId());
                        sendTitle(player, vars.lock_is_set);

                        ItemMeta meta = itemInHand.getItemMeta();
                        if (meta != null) {
                            meta.setDisplayName(key);
                            itemInHand.setItemMeta(meta);
                        } else {
                            sendTitle(player, vars.rename_denied);
                        }
                    } else {
                        sendTitle(player, vars.look_door);
                    }
                } else {
                    sender.sendMessage(vars.prefix + " " + vars.only_players);
                }
            } else if (args[0].equals("info")) {
                if (sender.hasPermission("doorlocker.admin")) {
                    if (sender instanceof Player) {
                        Player player = (Player) sender;
                        if (args.length == 2) {
                            String playerName = args[1];
                            OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(playerName);
                            if (targetPlayer != null) {
                                UUID targetUUID = targetPlayer.getUniqueId();
                                FileConfiguration config = plugin.getConfig();
                                boolean foundDoors = false;
                                player.sendMessage(" §7-+- " + vars.info_doors_player + " " + playerName + " §7-+-");
                                for (String location : config.getConfigurationSection("doors").getKeys(false)) {
                                    if (config.getString("doors." + location + ".owner").equals(targetUUID.toString())) {
                                        String[] locParts = location.split(",");
                                        String worldName = locParts[0];
                                        int x = Integer.parseInt(locParts[1]);
                                        int y = Integer.parseInt(locParts[2]);
                                        int z = Integer.parseInt(locParts[3]);
                                        String key = config.getString("doors." + location + ".key");

                                        player.sendMessage(" §7- " + vars.world + worldName + " §7| " + vars.coordinates + "X: " + x + ", Y: " + y + ", Z: " + z + " §7| " + vars.key + key);
                                        foundDoors = true;
                                    }
                                }

                                if (!foundDoors) {
                                    player.sendMessage(" §7- " + vars.doors_not_found);
                                }
                            } else {
                                player.sendMessage(vars.prefix + " §cИгрок с ником " + playerName + " не найден.");
                            }
                        } else {
                            Block block = player.getTargetBlockExact(5);
                            if (block != null && block.getType() == Material.IRON_DOOR) {
                                Block bottomBlock = getBottomBlock(block);
                                if (bottomBlock.hasMetadata("doorKey") && bottomBlock.hasMetadata("doorOwner")) {
                                    String key = bottomBlock.getMetadata("doorKey").get(0).asString();
                                    UUID ownerUUID = UUID.fromString(bottomBlock.getMetadata("doorOwner").get(0).asString());
                                    String ownerName = Bukkit.getOfflinePlayer(ownerUUID).getName();
                                    String location = "X: " + bottomBlock.getX() + ", Y: " + bottomBlock.getY() + ", Z: " + bottomBlock.getZ();
                                    player.sendMessage(" §7-+- " + vars.info_door + " §7-+-");
                                    player.sendMessage(" §7- " + vars.coordinates + location);
                                    player.sendMessage(" §7- " + vars.owner + ownerName + " (" + ownerUUID + ")");
                                    player.sendMessage(" §7- " + vars.key + key);
                                } else {
                                    sendTitle(player, vars.door_not_lock);
                                }
                            } else {
                                sendTitle(player, vars.look_door_info);
                            }
                        }
                    } else {
                        sender.sendMessage(vars.prefix + " " + vars.only_players);
                    }
                } else {
                    sender.sendMessage(vars.prefix + " " + vars.access_denied);
                }
            }
            else if (args[0].equals("destroy")) {
                if (sender.hasPermission("doorlocker.admin")) {
                    if (args.length == 2) {
                        String playerName = args[1];
                        OfflinePlayer targetPlayer = Bukkit.getOfflinePlayer(playerName);
                        if (targetPlayer != null) {
                            UUID targetUUID = targetPlayer.getUniqueId();
                            FileConfiguration config = plugin.getConfig();
                            boolean foundDoors = false;

                            for (String location : config.getConfigurationSection("doors").getKeys(false)) {
                                if (config.getString("doors." + location + ".owner").equals(targetUUID.toString())) {
                                    Block block = getBlockFromLocation(location);
                                    if (block != null) {
                                        block.removeMetadata("doorKey", plugin);
                                        block.removeMetadata("doorOwner", plugin);
                                        removeDoorData(block);
                                        foundDoors = true;
                                    }
                                }
                            }

                            if (foundDoors) {
                                sender.sendMessage(vars.prefix + " " + vars.all_doors_deleted_part1 + " " + playerName + " " + vars.all_doors_deleted_part2);
                            } else {
                                sender.sendMessage(vars.prefix + " " + vars.doors_player_not_found_part1 + " " + playerName + " " + vars.doors_player_not_found_part2);
                            }
                        } else {
                            sender.sendMessage(vars.prefix + " §cИгрок с ником " + playerName + " не найден.");
                        }
                    } else {
                        if (sender instanceof Player) {
                            Player player = (Player) sender;
                            Block block = player.getTargetBlockExact(5);
                            if (block != null && block.getType() == Material.IRON_DOOR) {
                                Block bottomBlock = getBottomBlock(block);
                                if (bottomBlock.hasMetadata("doorKey") && bottomBlock.hasMetadata("doorOwner")) {
                                    bottomBlock.removeMetadata("doorKey", plugin);
                                    bottomBlock.removeMetadata("doorOwner", plugin);
                                    removeDoorData(bottomBlock);
                                    sendTitle(player, vars.lock_deleted);
                                } else {
                                    sendTitle(player, vars.door_not_lock);
                                }
                            } else {
                                sendTitle(player, vars.unlock_door);
                            }
                        } else {
                            sender.sendMessage(vars.prefix + " " + vars.only_players);
                        }
                    }
                } else {
                    sender.sendMessage(vars.prefix + " " + vars.access_denied);
                }
            }
            else {
                sender.sendMessage(vars.prefix + " " + vars.command_not_found);
            }
        } else {
            sender.sendMessage(vars.prefix + " " + vars.command_not_found);
        }
        return true;
    }

    private Block getBlockFromLocation(String location) {
        String[] locParts = location.split(",");
        String worldName = locParts[0];
        int x = Integer.parseInt(locParts[1]);
        int y = Integer.parseInt(locParts[2]);
        int z = Integer.parseInt(locParts[3]);

        return Bukkit.getWorld(worldName).getBlockAt(x, y, z);
    }

    private Block getBottomBlock(Block block) {
        if (block.getType() == Material.IRON_DOOR && block.getBlockData() instanceof org.bukkit.block.data.type.Door door) {
            return door.getHalf() == org.bukkit.block.data.type.Door.Half.TOP ? block.getRelative(BlockFace.DOWN) : block;
        }
        return block;
    }

    private void sendTitle(Player player, String message) {
        player.sendActionBar(message);
    }

    private void saveDoorData(Block block, String key, UUID ownerUUID) {
        String location = block.getWorld().getName() + "," + block.getX() + "," + block.getY() + "," + block.getZ();
        FileConfiguration config = plugin.getConfig();
        config.set("doors." + location + ".key", key);
        config.set("doors." + location + ".owner", ownerUUID.toString());
        plugin.saveConfig();
    }

    private void removeDoorData(Block block) {
        String location = block.getWorld().getName() + "," + block.getX() + "," + block.getY() + "," + block.getZ();
        FileConfiguration config = plugin.getConfig();
        config.set("doors." + location, null);
        plugin.saveConfig();
    }
}

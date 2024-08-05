package com.multiplayer.doorlocker;

import org.bukkit.plugin.java.JavaPlugin;
import java.util.logging.Logger;

public final class DoorLocker extends JavaPlugin {


    @Override
    public void onEnable() {
        Logger log = this.getLogger();
        log.info("  _____                   _                _             ");
        log.info(" |  __ \\                 | |              | |            ");
        log.info(" | |  | | ___   ___  _ __| |     ___   ___| | _____ _ __ ");
        log.info(" | |  | |/ _ \\ / _ \\| '__| |    / _ \\ / __| |/ / _ \\ '__|");
        log.info(" | |__| | (_) | (_) | |  | |___| (_) | (__|   <  __/ |   ");
        log.info(" |_____/ \\___/ \\___/|_|  |______\\___/ \\___|_|\\_\\___|_|   ");
        log.info("");
        log.info(" -+- Initialization... -+-");
        log.info(" - Saving the configuration file...");
        this.saveDefaultConfig();
        log.info(" - File saved!");
        log.info(" - Initialization of variables...");
        Initialize();
        log.info(" - Done!");
        log.info(" - Adding commands to the DoorLocker class...");
        this.getCommand("doorlocker").setExecutor(new Commands(this));
        this.getCommand("doorlocker").setTabCompleter(new Commands_TabCompleter());
        getServer().getPluginManager().registerEvents(new DoorEventListener(this), this);
        log.info(" - Done!");
        log.info(" -+- DONE -+-");
        log.info(" -+- Plugin successfully enabled -+-");
    }

    @Override
    public void onDisable() {
        Logger log = this.getLogger();
        log.info("  _____                   _                _             ");
        log.info(" |  __ \\                 | |              | |            ");
        log.info(" | |  | | ___   ___  _ __| |     ___   ___| | _____ _ __ ");
        log.info(" | |  | |/ _ \\ / _ \\| '__| |    / _ \\ / __| |/ / _ \\ '__|");
        log.info(" | |__| | (_) | (_) | |  | |___| (_) | (__|   <  __/ |   ");
        log.info(" |_____/ \\___/ \\___/|_|  |______\\___/ \\___|_|\\_\\___|_|   ");
        log.info("");
        log.info(" -+- Plugin successfully disabled -+-");
    }

    private void Initialize(){
        reloadConfig();
        vars.prefix = getConfig().getString("properties.prefix");
        vars.reload_start = getConfig().getString("messages.reload_start");
        vars.reload_done = getConfig().getString("messages.reload_done");
        vars.useItemHook = getConfig().getBoolean("properties.useItemHook");
        vars.tickOpen = getConfig().getLong("properties.tickOpen");
        
        vars.command_not_found = getConfig().getString("messages.command_not_found");
        vars.access_denied = getConfig().getString("messages.access_denied");
        vars.more_args = getConfig().getString("messages.more_args");

        vars.help_help = getConfig().getString("messages.help_help");
        vars.help_set = getConfig().getString("messages.help_set");
        vars.help_delete = getConfig().getString("messages.help_delete");
        vars.help_destroy = getConfig().getString("messages.help_destroy");
        vars.help_info = getConfig().getString("messages.help_info");
        vars.help_reload = getConfig().getString("messages.help_reload");

        vars.lock_deleted = getConfig().getString("messages.lock_deleted");
        vars.you_not_owner = getConfig().getString("messages.you_not_owner");
        vars.door_doesnt_have_lock = getConfig().getString("messages.door_doesnt_have_lock");
        vars.only_players = getConfig().getString("messages.door_locked");
        vars.door_locked = getConfig().getString("messages.take_any_object");
        vars.take_any_object = getConfig().getString("messages.take_any_object");
        vars.lock_is_set = getConfig().getString("messages.lock_is_set");
        vars.rename_denied = getConfig().getString("messages.rename_denied");
        vars.look_door = getConfig().getString("messages.look_door");
        vars.info_doors_player = getConfig().getString("messages.info_doors_player");
        vars.info_door = getConfig().getString("messages.info_door");
        vars.world = getConfig().getString("messages.world");
        vars.coordinates = getConfig().getString("messages.coordinates");
        vars.owner = getConfig().getString("messages.owner");
        vars.key = getConfig().getString("messages.key");
        vars.doors_not_found = getConfig().getString("messages.doors_not_found");
        vars.door_not_lock = getConfig().getString("messages.door_not_lock");
        vars.look_door_info = getConfig().getString("messages.look_door_info");
        vars.all_doors_deleted_part1 = getConfig().getString("messages.all_doors_deleted_part1");
        vars.all_doors_deleted_part2 = getConfig().getString("messages.all_doors_deleted_part2");
        vars.doors_player_not_found_part1 = getConfig().getString("messages.doors_player_not_found_part1");
        vars.doors_player_not_found_part2 = getConfig().getString("messages.doors_player_not_found_part2");
        vars.unlock_door = getConfig().getString("messages.unlock_door");
        vars.onlyHook = getConfig().getString("messages.onlyHook");

        vars.key_denied = getConfig().getString("messages.key_denied");
        vars.you_cant_knock_door = getConfig().getString("messages.you_cant_knock_door");
    }

    public void reloadPLG(){
        Logger log = this.getLogger();
        log.info("  _____                   _                _             ");
        log.info(" |  __ \\                 | |              | |            ");
        log.info(" | |  | | ___   ___  _ __| |     ___   ___| | _____ _ __ ");
        log.info(" | |  | |/ _ \\ / _ \\| '__| |    / _ \\ / __| |/ / _ \\ '__|");
        log.info(" | |__| | (_) | (_) | |  | |___| (_) | (__|   <  __/ |   ");
        log.info(" |_____/ \\___/ \\___/|_|  |______\\___/ \\___|_|\\_\\___|_|   ");
        log.info("");
        log.info(" -+- Plugin successfully disabled -+-");
        log.info("  _____                   _                _             ");
        log.info(" |  __ \\                 | |              | |            ");
        log.info(" | |  | | ___   ___  _ __| |     ___   ___| | _____ _ __ ");
        log.info(" | |  | |/ _ \\ / _ \\| '__| |    / _ \\ / __| |/ / _ \\ '__|");
        log.info(" | |__| | (_) | (_) | |  | |___| (_) | (__|   <  __/ |   ");
        log.info(" |_____/ \\___/ \\___/|_|  |______\\___/ \\___|_|\\_\\___|_|   ");
        log.info("");
        log.info(" -+- Initialization... -+-");
        log.info(" - Saving the configuration file...");
        this.saveDefaultConfig();
        log.info(" - File saved!");
        log.info(" - Initialization of variables...");
        Initialize();
        log.info(" - Done!");
        log.info(" - Adding commands to the DoorLocker class...");
        log.info(" - Skip...");
        log.info(" - Done!");
        log.info(" -+- DONE -+-");
        log.info(" -+- Plugin successfully enabled -+-");
    }
}

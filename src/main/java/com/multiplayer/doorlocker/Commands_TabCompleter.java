package com.multiplayer.doorlocker;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

public class Commands_TabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> list = new ArrayList<>();
        if (args.length == 1) {
            if (sender.hasPermission("doorlocker.user"))
            {
                list.add("delete");
                list.add("set");
                list.add("help");
            }
            if (sender.hasPermission("doorlocker.admin")){
                list.add("destroy");
                list.add("info");
                list.add("reload");
            }

        }
        return list;
    }
}

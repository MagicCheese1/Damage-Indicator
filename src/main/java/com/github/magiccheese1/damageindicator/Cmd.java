package com.github.magiccheese1.damageindicator;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class Cmd implements CommandExecutor {
    private Main plugin;

    public Cmd(Main plugin) {

        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] arg) {
        if (commandSender.hasPermission("Damageindicator.admin")) {
            if (arg.length >= 1) {
                if (arg[0].equalsIgnoreCase("reload")) {
                    this.plugin.reloadConfig();
                    commandSender.sendMessage(ChatColor.YELLOW + " Plugin has been reloaded");
                }
            }
        }
        return false;
    }
}

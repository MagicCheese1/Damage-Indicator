package io.github.magiccheese1.damageindicator;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

public class CommandReload implements CommandExecutor {

    private final Plugin plugin;

    public CommandReload(final @NotNull Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String label,
                             @NotNull String[] args) {
        if (commandSender.hasPermission("Damageindicator.admin")) {
            if (args.length >= 1) {
                if (args[0].equalsIgnoreCase("reload")) {
                    this.plugin.reloadConfig();
                    commandSender.sendMessage(ChatColor.YELLOW + "DamageIndicator plugin has been reloaded");
                    return true;
                }
            }
        }
        return false;
    }
}

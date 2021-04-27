package it.gsync.spigot.commands;

import it.gsync.spigot.GSyncBukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class GodsEyeSyncCommand implements CommandExecutor {

    private GSyncBukkit plugin;


    public GodsEyeSyncCommand(GSyncBukkit plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if(!sender.hasPermission("godseye.admin")) {
            return false;
        }
        if(args.length == 0) {
            sender.sendMessage("§c/godseyesync reload §7- Reloads the configuration");
            return false;
        }
        if(args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            sender.sendMessage("§aConfig reloaded");
        }
        return false;
    }
}

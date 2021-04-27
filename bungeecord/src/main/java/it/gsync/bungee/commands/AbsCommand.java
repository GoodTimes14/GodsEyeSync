package it.gsync.bungee.commands;

import it.gsync.bungee.GSyncBungee;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;

public class AbsCommand extends Command {

    public GSyncBungee plugin;

    public AbsCommand(GSyncBungee plugin,String name, String permission, String... aliases) {
        super(name, permission, aliases);
        this.plugin = plugin;
        plugin.getProxy().getPluginManager().registerCommand(plugin,this);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {

    }
}

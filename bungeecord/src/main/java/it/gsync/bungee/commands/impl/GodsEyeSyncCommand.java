package it.gsync.bungee.commands.impl;

import it.gsync.bungee.GSyncBungee;
import it.gsync.bungee.commands.AbsCommand;
import net.md_5.bungee.api.CommandSender;

public class GodsEyeSyncCommand extends AbsCommand {

    public GodsEyeSyncCommand(GSyncBungee plugin, String name, String permission, String... aliases) {
        super(plugin, name, permission, aliases);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
    }
}

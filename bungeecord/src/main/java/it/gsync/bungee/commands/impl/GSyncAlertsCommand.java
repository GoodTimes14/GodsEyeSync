package it.gsync.bungee.commands.impl;

import it.gsync.bungee.GSyncBungee;
import it.gsync.bungee.commands.AbsCommand;
import it.gsync.bungee.data.PlayerData;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class GSyncAlertsCommand extends AbsCommand {


    public GSyncAlertsCommand(GSyncBungee plugin, String name, String permission, String... aliases) {
        super(plugin, name, permission, aliases);
    }

    @Override
    public void execute(CommandSender commandSender, String[] strings) {
        if(!(commandSender instanceof ProxiedPlayer)) {
            return;
        }
        ProxiedPlayer player = (ProxiedPlayer)commandSender;
        PlayerData playerData = plugin.getDataManager().getData(player);
        playerData.setAlerts(!playerData.isAlerts());
        player.sendMessage(playerData.isAlerts() ?
                new TextComponent(plugin.getConfigHandler().getMessage("alerts_enabled"))
                : new TextComponent(plugin.getConfigHandler().getMessage("alerts_disabled")));
    }
}

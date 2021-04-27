package it.gsync.velocity.commands.impl;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.proxy.Player;
import it.gsync.velocity.GSyncVelocity;
import it.gsync.velocity.commands.AbsCommand;
import it.gsync.velocity.data.PlayerData;
import net.kyori.adventure.text.Component;

public class AlertsCommand extends AbsCommand {


    public AlertsCommand(GSyncVelocity api, String name, String permission, String... aliases) {
        super(api, name, permission, aliases);
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        if(!(source instanceof Player)) {
            source.sendMessage(Component.text("Players only."));
            return;
        }
        Player player = (Player)source;
        PlayerData playerData = getApi().getDataManager().getData(player);
        playerData.setAlerts(!playerData.isAlerts());
        String message = playerData.isAlerts() ? getApi().getConfigHandler().getMessage("alerts_enabled") :  getApi().getConfigHandler().getMessage("alerts_disabled");
        player.sendMessage(Component.text(message));
    }
}

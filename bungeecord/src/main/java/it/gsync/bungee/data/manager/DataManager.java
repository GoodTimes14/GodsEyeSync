package it.gsync.bungee.data.manager;

import com.google.common.collect.Maps;
import it.gsync.bungee.GSyncBungee;
import it.gsync.bungee.data.*;
import it.gsync.common.data.IDataManager;
import it.gsync.common.objects.Alert;
import it.gsync.common.objects.Flag;
import it.gsync.common.objects.Punish;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.util.Map;
import java.util.UUID;

@Getter
@Setter
public class DataManager implements IDataManager {


    private GSyncBungee plugin;
    private Map<UUID,PlayerData> dataMap;

    public DataManager(GSyncBungee plugin)  {
        this.plugin = plugin;
        dataMap = Maps.newConcurrentMap();
    }

    public PlayerData getData(ProxiedPlayer player) {
        return dataMap.computeIfAbsent(player.getUniqueId(),(uuid) -> new PlayerData(plugin,player));
    }

    public PlayerData removeData(ProxiedPlayer player) {
        return dataMap.remove(player.getUniqueId());
    }


    @Override
    public void sendAlertMessage(Alert alert) {
        String hack = alert.getCheckType().substring(0, 1).toUpperCase() + alert.getCheckType().substring(1).toLowerCase();
        String alertString = plugin.getConfigHandler().getMessage("alert")
                .replace("%hack%",hack)
                .replace("%player%",alert.getPlayerName())
                .replace("%server%",alert.getServer())
                .replace("%flags%",String.valueOf(alert.getVl()));
        String serverCommand = ((String) plugin.getConfigHandler().getSetting("warp_to_server_command")).replace("%server%",alert.getServer());
        TextComponent alertMessage = new TextComponent(alertString);
        alertMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(plugin.getConfigHandler().getMessage("server_tooltip")).create()));
        alertMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,serverCommand));
        for(PlayerData playerData : dataMap.values()) {
            if(playerData.isAlerts()) {
                playerData.getPlayer().sendMessage(alertMessage);
            }
        }
    }

    @Override
    public void sendFlagMessage(Flag flag) {
        String hack = flag.getCheckType().substring(0, 1).toUpperCase() + flag.getCheckType().substring(1).toLowerCase();
        String flagString = plugin.getConfigHandler().getMessage("verbose")
                .replace("%hack%",hack)
                .replace("%player%",flag.getPlayerName())
                .replace("%server%",flag.getServer())
                .replace("%flags%",String.valueOf(flag.getVl()))
                .replace("%type%",flag.getDetection())
                .replace("%ping%",String.valueOf(flag.getPing()))
                .replace("%tps%",String.valueOf(flag.getTps()));
        String serverCommand = ((String) plugin.getConfigHandler().getSetting("warp_to_server_command")).replace("%server%",flag.getServer());
        TextComponent flagMessage = new TextComponent(flagString);
        flagMessage.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(plugin.getConfigHandler().getMessage("server_tooltip")).create()));
        flagMessage.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND,serverCommand));
        for(PlayerData playerData : dataMap.values()) {
            if(playerData.isVerbose()) {
                playerData.getPlayer().sendMessage(flagMessage);
            }
        }
    }

    @Override
    public void sendPunishMessage(Punish punish) {
        String hack = punish.getCheckType().substring(0, 1).toUpperCase() + punish.getCheckType().substring(1).toLowerCase();
        if(punish.getPunishType().equalsIgnoreCase("kick")) {
            String kickMessage = plugin.getConfigHandler().getMessage("kicked_from_server")
                    .replace("%player%",punish.getPlayerName())
                    .replace("%server%",punish.getServer())
                    .replace("%hack%",hack);
            plugin.getLoader().getProxy().broadcast(new TextComponent(kickMessage));
        } else {
            String banMessage = plugin.getConfigHandler().getBanMessage()
                    .replace("%player%", punish.getPlayerName())
                    .replace("%server%",punish.getServer())
                    .replace("%hack%",hack);
            if((Boolean) plugin.getConfigHandler().getSetting("ban_broadcast")) {
                plugin.getLoader().getProxy().broadcast(new TextComponent(banMessage));
            }
        }
    }

    @Override
    public void executePunish(Punish punish) {
        String hack = punish.getCheckType().substring(0, 1).toUpperCase() + punish.getCheckType().substring(1).toLowerCase();
        if(punish.getPunishType().equalsIgnoreCase("kick")) {
            String kickCommand = ((String)  plugin.getConfigHandler().getSetting("kick_command"))
                    .replace("%player%", punish.getPlayerName())
                    .replace("%server%",punish.getServer())
                    .replace("%hack%",hack);
            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(),kickCommand);
        } else {
            String banCommand = ((String) plugin.getConfigHandler().getSetting("ban_command"))
                    .replace("%player%", punish.getPlayerName())
                    .replace("%server%",punish.getServer())
                    .replace("%hack%",hack);
            ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(),banCommand);
        }
    }
}

package it.gsync.velocity.data.manager;

import com.google.common.collect.Maps;
import com.velocitypowered.api.proxy.Player;
import it.gsync.common.data.manager.IDataManager;
import it.gsync.common.objects.Alert;
import it.gsync.common.objects.Flag;
import it.gsync.common.objects.Punish;
import it.gsync.velocity.GSyncVelocity;
import it.gsync.velocity.data.PlayerData;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.event.HoverEvent;

import java.util.Map;
import java.util.UUID;

public class DataManager implements IDataManager {


    private Map<UUID, PlayerData> dataMap;
    private GSyncVelocity api;

    public DataManager(GSyncVelocity api) {
        this.api = api;
        this.dataMap = Maps.newHashMap();
    }


    public PlayerData getData(Player player) {
        return dataMap.computeIfAbsent(player.getUniqueId(),(uuid) -> new PlayerData(api,player));
    }

    public PlayerData removeData(Player player) {
        return dataMap.remove(player.getUniqueId());
    }

    @Override
    public void sendAlertMessage(Alert alert) {
        String hack = alert.getCheckType().substring(0, 1).toUpperCase() + alert.getCheckType().substring(1).toLowerCase();
        String alertString = api.getConfigHandler().getMessage("alert")
                .replace("%hack%",hack)
                .replace("%player%",alert.getPlayerName())
                .replace("%server%",alert.getServer())
                .replace("%flags%",String.valueOf(alert.getVl()));
        String serverCommand = ((String) api.getConfigHandler().getSetting("warp_to_server_command")).replace("%server%",alert.getServer());
        String tooltip = api.getConfigHandler().getMessage("server_tooltip");
        Component component = Component.text(alertString).hoverEvent(HoverEvent.showText(Component.text(tooltip))).clickEvent(ClickEvent.runCommand(serverCommand));
        for(PlayerData playerData : dataMap.values()) {
            if(playerData.isAlerts()) {
                playerData.getPlayer().sendMessage(component);
            }
        }
    }

    @Override
    public void sendFlagMessage(Flag flag) {
        String hack = flag.getCheckType().substring(0, 1).toUpperCase() + flag.getCheckType().substring(1).toLowerCase();
        String flagString = api.getConfigHandler().getMessage("verbose")
                .replace("%hack%",hack)
                .replace("%player%",flag.getPlayerName())
                .replace("%server%",flag.getServer())
                .replace("%flags%",String.valueOf(flag.getVl()))
                .replace("%type%",flag.getDetection())
                .replace("%ping%",String.valueOf(flag.getPing()))
                .replace("%tps%",String.valueOf(flag.getTps()));
        String serverCommand = ((String) api.getConfigHandler().getSetting("warp_to_server_command")).replace("%server%",flag.getServer());
        String tooltip = api.getConfigHandler().getMessage("server_tooltip");
        Component component = Component.text(flagString).hoverEvent(HoverEvent.showText(Component.text(tooltip))).clickEvent(ClickEvent.runCommand(serverCommand));
        for(PlayerData playerData : dataMap.values()) {
            if(playerData.isVerbose()) {
                playerData.getPlayer().sendMessage(component);
            }
        }
    }

    @Override
    public void sendPunishMessage(Punish punish) {
        String hack = punish.getCheckType().substring(0, 1).toUpperCase() + punish.getCheckType().substring(1).toLowerCase();
        if(punish.getPunishType().equalsIgnoreCase("kick")) {
            String kickMessage = api.getConfigHandler().getMessage("kicked_from_server")
                    .replace("%player%",punish.getPlayerName())
                    .replace("%server%",punish.getServer())
                    .replace("%hack%",hack);
            Component component = Component.text(kickMessage);
            if((Boolean) api.getConfigHandler().getSetting("ban_broadcast")) {
                for(Player player : api.getServer().getAllPlayers()) {
                    player.sendMessage(component);
                }
            }
        } else {
            String banMessage = api.getConfigHandler().getBanMessage()
                    .replace("%player%", punish.getPlayerName())
                    .replace("%server%",punish.getServer())
                    .replace("%hack%",hack);
            Component component = Component.text(banMessage);
            if((Boolean) api.getConfigHandler().getSetting("ban_broadcast")) {
                for(Player player : api.getServer().getAllPlayers()) {
                    player.sendMessage(component);
                }
            }
        }
    }

    @Override
    public void executePunish(Punish punish) {
        String hack = punish.getCheckType().substring(0, 1).toUpperCase() + punish.getCheckType().substring(1).toLowerCase();
        if(punish.getPunishType().equalsIgnoreCase("kick")) {
            String kickCommand = ((String)  api.getConfigHandler().getSetting("kick_command"))
                    .replace("%player%", punish.getPlayerName())
                    .replace("%server%",punish.getServer())
                    .replace("%hack%",hack);
            api.getServer().getCommandManager().executeAsync(api.getServer().getConsoleCommandSource(),kickCommand);
        } else {
            String banCommand = ((String) api.getConfigHandler().getSetting("ban_command"))
                    .replace("%player%", punish.getPlayerName())
                    .replace("%server%",punish.getServer())
                    .replace("%hack%",hack);
            api.getServer().getCommandManager().executeAsync(api.getServer().getConsoleCommandSource(),banCommand);
        }
    }
}

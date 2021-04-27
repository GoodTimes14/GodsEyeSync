package it.gsync.bungee.data;

import it.gsync.bungee.GSyncBungee;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

@Getter
@Setter
public class PlayerData {

    private GSyncBungee plugin;
    private ProxiedPlayer player;
    private boolean alerts;
    private boolean verbose;

    public PlayerData(GSyncBungee plugin, ProxiedPlayer player) {
        this.plugin = plugin;
        this.player = player;
        if(plugin.getConfig().getBoolean("settings.enable_alerts_auto")) {
            if(player.hasPermission(plugin.getConfig().getString("settings.permission_alert"))) {
                alerts = true;
                player.sendMessage(new TextComponent(plugin.getConfigHandler().getMessage("alerts_enabled_automatically")));
            }
        }
    }

}
package it.gsync.velocity.data;

import com.velocitypowered.api.proxy.Player;
import it.gsync.velocity.GSyncVelocity;
import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;

@Getter
@Setter
public class PlayerData {


    private Player player;
    private boolean alerts;
    private boolean verbose;

    public PlayerData(GSyncVelocity api,Player player) {
        this.player = player;
        if((boolean) api.getConfigHandler().getSetting("enable_alerts_auto")) {
            if(player.hasPermission((String) api.getConfigHandler().getSetting("permission_alert"))) {
                alerts = true;
                player.sendMessage(Component.text(api.getConfigHandler().getMessage("alerts_enabled_automatically")));
            }
        }
    }



}

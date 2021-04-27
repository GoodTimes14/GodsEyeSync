package it.gsync.velocity.listeners.impl;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import it.gsync.common.objects.Alert;
import it.gsync.common.objects.Flag;
import it.gsync.common.objects.Punish;
import it.gsync.velocity.GSyncVelocity;
import it.gsync.velocity.listeners.AbsListener;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class PluginListener extends AbsListener {


    public PluginListener(GSyncVelocity api) {
        super(api);
    }

    @Subscribe(order = PostOrder.LAST)
    public void onJoin(PluginMessageEvent event) {
        String id = event.getIdentifier().getId();
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
        try {
            String playerName = in.readUTF();
            Player player = api.getServer().getPlayer(playerName).orElse(null);
            UUID playerUuid = player.getUniqueId();
            switch (id) {
                case "gsync:alerts":
                    String checkType = in.readUTF();
                    int vl = in.readInt();
                    String server = player.getCurrentServer().get().getServerInfo().getName();
                    Alert alert = new Alert(playerUuid,playerName,server,checkType,vl);
                    api.getMessageHandler().handleMessage(alert);
                    break;
                case "gsync:violations":
                    checkType = in.readUTF();
                    String detection = in.readUTF();
                    long ping = in.readLong();
                    double tps = in.readDouble();
                    vl = in.readInt();
                    long timestamp = in.readLong();
                    server = player.getCurrentServer().get().getServerInfo().getName();
                    Flag flag = new Flag(playerUuid,playerName,server,detection,checkType,vl, (int) ping,tps,timestamp);
                    api.getMessageHandler().handleMessage(flag);
                    break;
                case "gsync:punishments":
                    checkType = in.readUTF();
                    String punishType = in.readUTF();
                    timestamp = in.readLong();
                    server = player.getCurrentServer().get().getServerInfo().getName();
                    SimpleDateFormat formatter = new SimpleDateFormat((String) api.getConfigHandler().getSetting("date-format"));
                    formatter.setTimeZone(Calendar.getInstance().getTimeZone());
                    Punish punish = new Punish(playerUuid,playerName,server,checkType,punishType,formatter.format(timestamp));
                    api.getMessageHandler().handleMessage(punish);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}

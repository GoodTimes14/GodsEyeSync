package it.gsync.bungee.listeners.impl;

import it.gsync.bungee.GSyncBungee;
import it.gsync.bungee.listeners.AbsListener;
import it.gsync.common.objects.Alert;
import it.gsync.common.objects.Flag;
import it.gsync.common.objects.Punish;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.UUID;

public class PluginListener  extends AbsListener {

    public PluginListener(GSyncBungee plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEvent(PluginMessageEvent event) {
        DataInputStream in = new DataInputStream(new ByteArrayInputStream(event.getData()));
        try {
            switch (event.getTag().replace("gsync:","")) {
                case "alerts":
                    String playerName = in.readUTF();
                    ProxiedPlayer player = plugin.getProxy().getInstance().getPlayer(playerName);
                    UUID playerUuid = player.getUniqueId();
                    String checkType = in.readUTF();
                    int vl = in.readInt();
                    String server = player.getServer().getInfo().getName();
                    Alert alert = new Alert(playerUuid,playerName,server,checkType,vl);
                    plugin.getMessageHandler().handleMessage(alert);
                    break;
                case "violations":
                    playerName = in.readUTF();
                    player = plugin.getProxy().getPlayer(playerName);
                    playerUuid = player.getUniqueId();
                    checkType = in.readUTF();
                    String detection = in.readUTF();
                    long ping = in.readLong();
                    double tps = in.readDouble();
                    vl = in.readInt();
                    long timestamp = in.readLong();
                    server = player.getServer().getInfo().getName();
                    Flag flag = new Flag(playerUuid,playerName,server,detection,checkType,vl, (int) ping,tps,timestamp);
                    plugin.getMessageHandler().handleMessage(flag);
                    break;
                case "punishments":
                    playerName = in.readUTF();
                    player = plugin.getProxy().getPlayer(playerName);
                    playerUuid = player.getUniqueId();
                    checkType = in.readUTF();
                    String punishType = in.readUTF();
                    timestamp = in.readLong();
                    server = player.getServer().getInfo().getName();
                    SimpleDateFormat formatter = new SimpleDateFormat((String) plugin.getConfigHandler().getSetting("date-format"));
                    formatter.setTimeZone(Calendar.getInstance().getTimeZone());
                    Punish punish = new Punish(playerUuid,playerName,server,checkType,punishType,formatter.format(timestamp));
                    plugin.getMessageHandler().handleMessage(punish);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}

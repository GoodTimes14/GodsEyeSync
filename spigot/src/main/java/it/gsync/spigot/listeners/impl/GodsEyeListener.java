package it.gsync.spigot.listeners.impl;

import godseye.GodsEyeAlertEvent;
import godseye.GodsEyePlayerViolationEvent;
import godseye.GodsEyePunishPlayerEvent;
import it.gsync.spigot.GSyncBukkit;
import it.gsync.spigot.listeners.AbsListener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class GodsEyeListener extends AbsListener {

    public GodsEyeListener(GSyncBukkit plugin) {
        super(plugin);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAlert(GodsEyeAlertEvent event) {
        if(plugin.getConfig().getBoolean("bukkit.bungee_alerts")) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF(event.getPlayer().getName());
                out.writeUTF(event.getCheckType().toString());
                out.writeInt(event.getViolationCount());
            } catch (IOException e) {
                e.printStackTrace();
            }
            event.getPlayer().sendPluginMessage(plugin,"gsync:alerts",b.toByteArray());
        }
    }



    @EventHandler(priority = EventPriority.HIGHEST)
    public void onViolation(GodsEyePlayerViolationEvent event) {
        if(plugin.getConfig().getBoolean("bukkit.bungee_verboses")) {
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF(event.getPlayer().getName());
                out.writeUTF(event.getCheckType().toString());
                out.writeUTF(event.getDetection());
                out.writeLong(event.getPing());
                out.writeDouble(event.getTPS());
                out.writeInt(event.getViolationCount());
                out.writeLong(System.currentTimeMillis());
            } catch (IOException e) {
                e.printStackTrace();
            }
            event.getPlayer().sendPluginMessage(plugin,"gsync:violations",b.toByteArray());
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPunish(GodsEyePunishPlayerEvent event) {
        if(event.isCancelled()) {
            return;
        }
        if(plugin.getConfig().getBoolean("bukkit.bungee_bans")) {
            event.setCancelled(true);
            ByteArrayOutputStream b = new ByteArrayOutputStream();
            DataOutputStream out = new DataOutputStream(b);
            try {
                out.writeUTF(event.getPlayer().getName());
                out.writeUTF(event.getCheckType().toString());
                out.writeUTF(event.getPunishType().toString());
                out.writeLong(System.currentTimeMillis());
            } catch (IOException e) {
                e.printStackTrace();
            }
            event.getPlayer().sendPluginMessage(plugin,"gsync:punishments",b.toByteArray());
        }

    }



}

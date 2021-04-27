package it.gsync.bungee.listeners.impl;

import it.gsync.bungee.GSyncBungee;
import it.gsync.bungee.listeners.AbsListener;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.event.EventHandler;

public class PlayerConnection extends AbsListener {


    public PlayerConnection(GSyncBungee plugin) {
        super(plugin);
    }

    @EventHandler
    public void onEvent(PostLoginEvent event) {
        this.getPlugin().getDataManager().getData(event.getPlayer());
    }

    @EventHandler
    public void onEvent(PlayerDisconnectEvent event) {
        this.getPlugin().getDataManager().removeData(event.getPlayer());
    }




}

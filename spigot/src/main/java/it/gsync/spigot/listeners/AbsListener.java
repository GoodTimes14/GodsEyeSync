package it.gsync.spigot.listeners;

import it.gsync.spigot.GSyncBukkit;
import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

public abstract class AbsListener implements Listener {


    public GSyncBukkit plugin;

    public AbsListener(GSyncBukkit plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this,plugin);
    }

}

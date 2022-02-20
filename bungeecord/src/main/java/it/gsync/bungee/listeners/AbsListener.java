package it.gsync.bungee.listeners;

import it.gsync.bungee.GSyncBungee;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.plugin.Listener;

@Getter
@Setter
public abstract class AbsListener implements Listener {

    public GSyncBungee plugin;


    public AbsListener(GSyncBungee plugin) {
        this.plugin = plugin;
        plugin.getLoader().getProxy().getPluginManager().registerListener(plugin.getLoader(),this);
    }

}

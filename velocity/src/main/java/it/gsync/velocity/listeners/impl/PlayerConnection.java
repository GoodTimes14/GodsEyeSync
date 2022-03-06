package it.gsync.velocity.listeners.impl;

import com.velocitypowered.api.event.PostOrder;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.DisconnectEvent;
import com.velocitypowered.api.event.connection.PostLoginEvent;
import it.gsync.velocity.GSyncVelocityLoader;
import it.gsync.velocity.listeners.AbsListener;
import it.gsync.velocity.plugin.GSyncVelocity;

public class PlayerConnection extends AbsListener {

    public PlayerConnection(GSyncVelocity api) {
        super(api);
    }

    @Subscribe(order = PostOrder.LAST)
    public void onEvent(PostLoginEvent event) {
        api.getDataManager().getData(event.getPlayer());
    }

    @Subscribe(order = PostOrder.LAST)
    public void onEvent(DisconnectEvent event) {
        api.getDataManager().removeData(event.getPlayer());
    }


}

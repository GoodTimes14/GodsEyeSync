package it.gsync.velocity.listeners;

import it.gsync.velocity.GSyncVelocityLoader;
import it.gsync.velocity.plugin.GSyncVelocity;

public abstract class AbsListener  {

    public GSyncVelocity api;

    public AbsListener(GSyncVelocity api) {
        this.api = api;
        api.getServer().getEventManager().register(api.getLoader(),this);
    }

}

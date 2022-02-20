package it.gsync.velocity.listeners;

import it.gsync.velocity.GSyncVelocity;

public abstract class AbsListener  {

    public GSyncVelocity api;

    public AbsListener(GSyncVelocity api) {
        this.api = api;
        api.getServer().getEventManager().register(api,this);
    }

}

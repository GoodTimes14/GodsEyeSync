package it.gsync.velocity.commands;

import com.velocitypowered.api.command.CommandMeta;
import com.velocitypowered.api.command.SimpleCommand;
import it.gsync.velocity.GSyncVelocity;
import lombok.Getter;

@Getter
public abstract class AbsCommand implements SimpleCommand {


    private GSyncVelocity api;
    private String name;
    private String permission;
    private String[] aliases;


    public AbsCommand(GSyncVelocity api, String name, String permission, String... aliases)  {
        this.api = api;
        this.name = name;
        this.permission = permission;
        this.aliases  = aliases;
        CommandMeta meta = api.getServer().getCommandManager().metaBuilder(name).aliases(aliases).build();
        api.getServer().getCommandManager().register(meta,this);
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission(permission);
    }
}

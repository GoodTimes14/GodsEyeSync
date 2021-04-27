package it.gsync.velocity.commands.impl;

import com.velocitypowered.api.command.CommandSource;
import it.gsync.common.objects.Flag;
import it.gsync.common.objects.Punish;
import it.gsync.velocity.GSyncVelocity;
import it.gsync.velocity.commands.AbsCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;

import java.util.*;

public class LogsCommand extends AbsCommand {

    public LogsCommand(GSyncVelocity api, String name, String permission, String... aliases) {
        super(api, name, permission, aliases);
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();
        if(getApi().getDataConnector() == null) {
            source.sendMessage(Component.text("§cData storing is disabled,please enable it in the config"));
            return;
        }
        if(args.length < 2) {
            source.sendMessage(Component.text("§7Usage: §c/bungeelogs <type:§7(verbose,punish)§c> <player>"));
            return;
        }
        String type = args[0];
        String playerName = args[1];
        if(type.equalsIgnoreCase("verbose")) {
            List<Flag> flagList = getApi().getDataConnector().fetchObjects(Flag.class,"playerName",playerName);
            Collections.reverse(flagList);
            if(flagList.isEmpty()) {
                source.sendMessage(Component.text("§cLogs not found"));
                return;
            }
            Map<String,List<Flag>> flags = new HashMap<>();
            for(Flag flag: flagList) {
                if(flags.containsKey(flag.getCheckType())) {
                    flags.get(flag.getCheckType()).add(flag);
                } else {
                    List<Flag> list = new ArrayList<>();
                    list.add(flag);
                    flags.put(flag.getCheckType(),list);
                }
            }
            source.sendMessage(Component.text(getApi().getConfigHandler().getMessage("log_message")
                    .replace("%player%",playerName)
                    .replace("%type%","Verbose")));
            String logEntry = getApi().getConfigHandler().getMessage("log_entry_verbose");
            String logEntryHover = getApi().getConfigHandler().getMessage("log_entry_verbose_hover");
            int limit = (int) getApi().getConfigHandler().getSetting("verbose_hover_entry_limit");
            for(Map.Entry<String,List<Flag>> entry : flags.entrySet()) {
                String hack = entry.getKey().substring(0, 1).toUpperCase() +  entry.getKey().substring(1).toLowerCase();
                Component flagEntry = Component.text(logEntry.replace("%hack%",hack).replace("%quantity%","" +entry.getValue().size()));
                StringBuilder hoverBuilder = new StringBuilder();
                int counter = Math.min(entry.getValue().size(),limit);
                for(int i = 0;i < counter;i++) {
                    Flag flag = entry.getValue().get(i);
                    hoverBuilder.append(logEntryHover
                            .replace("%hack%",hack)
                            .replace("%type%",flag.getDetection())
                            .replace("%ping%","" + flag.getPing())
                            .replace("%tps%","" + flag.getTps())
                            .replace("%server%",flag.getServer()));
                    if(i + 1 < counter) {
                        hoverBuilder.append("\n");
                    }
                }
                source.sendMessage(flagEntry.hoverEvent(HoverEvent.showText(Component.text(hoverBuilder.toString()))));
            }
        } else if(type.equalsIgnoreCase("punish")) {
            List<Punish> punishList = getApi().getDataConnector().fetchObjects(Punish.class,"playerName",playerName);
            Collections.reverse(punishList);
            source.sendMessage(Component.text(getApi().getConfigHandler().getMessage("log_message")
                    .replace("%player%",playerName)
                    .replace("%type%","Punish")));
            if(punishList.isEmpty()) {
                source.sendMessage(Component.text("§cLogs not found"));
                return;
            }
            String logEntry = getApi().getConfigHandler().getMessage("log_entry_punish");
            for(Punish punish : punishList) {
                String punishType = punish.getPunishType().substring(0, 1).toUpperCase() +  punish.getPunishType().substring(1).toLowerCase();
                String hack = punish.getCheckType().substring(0, 1).toUpperCase() +  punish.getCheckType().substring(1).toLowerCase();
                source.sendMessage(Component.text(logEntry
                        .replace("%date%",punish.getDate())
                        .replace("%server%",punish.getServer())
                        .replace("%punishtype%",punishType)
                        .replace("%hack%",hack)));
            }
        }
    }
}

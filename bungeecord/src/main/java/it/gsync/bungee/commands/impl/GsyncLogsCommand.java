package it.gsync.bungee.commands.impl;

import it.gsync.bungee.GSyncBungee;
import it.gsync.bungee.commands.AbsCommand;
import it.gsync.common.objects.Flag;
import it.gsync.common.objects.Punish;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;

import java.util.*;

public class GsyncLogsCommand extends AbsCommand {


    public GsyncLogsCommand(GSyncBungee plugin, String name, String permission, String... aliases) {
        super(plugin, name, permission, aliases);
    }

    @Override
    public void execute(CommandSender commandSender, String[] args) {
        if(plugin.getDataConnector() == null) {
            commandSender.sendMessage(new TextComponent("§cData storing is disabled,please enable it in the config"));
            return;
        }
        if(args.length < 2) {
            commandSender.sendMessage(new TextComponent("§7Usage: §c/bungeelogs <type:§7(verbose,punish)§c> <player>"));
            return;
        }
        String type = args[0];
        String playerName = args[1];
        if(type.equalsIgnoreCase("verbose")) {
            List<Flag> flagList = plugin.getDataConnector().fetchObjects(Flag.class,"playerName",playerName);
            Collections.reverse(flagList);
            if(flagList.isEmpty()) {
                commandSender.sendMessage(new TextComponent("§cLogs not found"));
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
            commandSender.sendMessage(new TextComponent(plugin.getConfigHandler().getMessage("log_message")
                    .replace("%player%",playerName)
                    .replace("%type%","Verbose")));
            String logEntry = plugin.getConfigHandler().getMessage("log_entry_verbose");
            String logEntryHover = plugin.getConfigHandler().getMessage("log_entry_verbose_hover");
            int limit = (int) plugin.getConfigHandler().getSetting("verbose_hover_entry_limit");
            for(Map.Entry<String,List<Flag>> entry : flags.entrySet()) {
                String hack = entry.getKey().substring(0, 1).toUpperCase() +  entry.getKey().substring(1).toLowerCase();
                TextComponent flagEntry = new TextComponent(logEntry.replace("%hack%",hack).replace("%quantity%","" +entry.getValue().size()));
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
                flagEntry.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(hoverBuilder.toString()).create()));
                commandSender.sendMessage(flagEntry);
            }
        } else if(type.equalsIgnoreCase("punish")) {
            List<Punish> punishList = plugin.getDataConnector().fetchObjects(Punish.class,"playerName",playerName);
            Collections.reverse(punishList);
            commandSender.sendMessage(new TextComponent(plugin.getConfigHandler().getMessage("log_message")
                    .replace("%player%",playerName)
                    .replace("%type%","Punish")));
            if(punishList.isEmpty()) {
                commandSender.sendMessage(new TextComponent("§cLogs not found"));
                return;
            }
            String logEntry = plugin.getConfigHandler().getMessage("log_entry_punish");
            for(Punish punish : punishList) {
                String punishType = punish.getPunishType().substring(0, 1).toUpperCase() +  punish.getPunishType().substring(1).toLowerCase();
                String hack = punish.getCheckType().substring(0, 1).toUpperCase() +  punish.getCheckType().substring(1).toLowerCase();
                commandSender.sendMessage(new TextComponent(logEntry
                        .replace("%date%",punish.getDate())
                        .replace("%server%",punish.getServer())
                        .replace("%punishtype%",punishType)
                        .replace("%hack%",hack)));
            }
        }
    }
}

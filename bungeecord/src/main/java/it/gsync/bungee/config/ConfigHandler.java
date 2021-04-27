package it.gsync.bungee.config;

import it.gsync.bungee.GSyncBungee;
import it.gsync.common.configuration.handler.IConfigHandler;
import lombok.Getter;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;

import java.util.List;

@Getter
public class ConfigHandler implements IConfigHandler {

    private GSyncBungee plugin;
    private String prefix;
    private Configuration messageSection;
    private Configuration settingsSection;
    private String banMex;


    public ConfigHandler(GSyncBungee plugin) {
        this.plugin = plugin;
        load();
    }

    public void load() {
        prefix = ChatColor.translateAlternateColorCodes('&',plugin.getConfig().getString("messages.prefix"));
        messageSection = plugin.getConfig().getSection("messages");
        settingsSection = plugin.getConfig().getSection("settings");
        banMex = loadBanMessage();
    }

    public String getMessage(String path) {
        return ChatColor.translateAlternateColorCodes('&',messageSection.getString(path)).replace("%prefix%",prefix);
    }

    public Object getSetting(String path) {
        return settingsSection.get(path);
    }

    @Override
    public String getBanMessage() {
        return banMex;
    }

    public String loadBanMessage() {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> banMessageList = messageSection.getStringList("ban_message");
        for(int i = 0;i < banMessageList.size();i++) {
            stringBuilder.append(ChatColor.translateAlternateColorCodes('&',banMessageList.get(i) ));
            if(i + 1 < banMessageList.size()) {
                stringBuilder.append("\n");
            }
        }
        return stringBuilder.toString();
    }


}

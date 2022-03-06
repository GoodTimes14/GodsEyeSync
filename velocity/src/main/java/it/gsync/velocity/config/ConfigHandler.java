package it.gsync.velocity.config;

import it.gsync.common.configuration.YamlConfiguration;
import it.gsync.common.configuration.handler.IConfigHandler;
import it.gsync.common.utils.ColorUtils;
import it.gsync.velocity.GSyncVelocityLoader;
import it.gsync.velocity.plugin.GSyncVelocity;
import lombok.Getter;

import java.util.List;

@Getter
public class ConfigHandler implements IConfigHandler {


    private GSyncVelocity plugin;
    private YamlConfiguration configuration;
    private String banMex;
    private String prefix;


    public ConfigHandler(GSyncVelocity plugin) {
        this.plugin = plugin;
        configuration = new YamlConfiguration(plugin.getDataDirectory(),"bungeeconfig.yml","config.yml");
        StringBuilder stringBuilder = new StringBuilder();
        List<String> banMessageList = configuration.get("messages.ban_message",List.class);
        for(int i = 0;i < banMessageList.size();i++) {
            stringBuilder.append(ColorUtils.translateAlternateColorCodes('&',banMessageList.get(i)));
            if(i + 1 < banMessageList.size()) {
                stringBuilder.append("\n");
            }
        }
        prefix = configuration.get("messages.prefix",String.class);
        banMex = stringBuilder.toString();
    }

    public String getMessage(String path) {
        return ColorUtils.translateAlternateColorCodes('&',configuration.get("messages." + path,String.class).replace("%prefix%",prefix));
    }

    @Override
    public Object getSetting(String path) {
        return configuration.get("settings." + path);
    }

    @Override
    public String getBanMessage() {
        return banMex;
    }
}

package it.gsync.spigot;

import it.gsync.spigot.commands.GodsEyeSyncCommand;
import it.gsync.spigot.listeners.impl.GodsEyeListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.spigotmc.SpigotConfig;

import java.io.*;


public class GSyncBukkit extends JavaPlugin {


    @Override
    public void onEnable() {
        if(!SpigotConfig.bungee) {
            Bukkit.getConsoleSender().sendMessage("§c[GodsEyeSync] Server is not running on bungeecord mode disabling...");
            Bukkit.getConsoleSender().sendMessage("§c[GodsEyeSync] Set the \"bungeecord:\" option to true in your spigot.yml then restart the server");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        getCommand("godseyesync").setExecutor(new GodsEyeSyncCommand(this));
        saveDefaultConfig();
        registerChannels();
        registerListeners();

    }

    @Override
    public void onDisable() {
        getLogger().info("Disabling...");
        getServer().getMessenger().unregisterIncomingPluginChannel(this);
    }

    private void registerListeners() {
        new GodsEyeListener(this);
    }


    private void registerChannels() {
        getLogger().info("Registering channels...");
        getServer().getMessenger().registerOutgoingPluginChannel(this,"gsync:alerts");
        getServer().getMessenger().registerOutgoingPluginChannel(this,"gsync:violations");
        getServer().getMessenger().registerOutgoingPluginChannel(this,"gsync:punishments");
    }


    @Override
    public void saveDefaultConfig() {
        if(!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }
        File configFile = new File(getDataFolder(), "config.yml");
        if(!configFile.exists()) {
            saveResource("bukkitconfig.yml",false);
            configFile = new File(getDataFolder(), "bukkitconfig.yml");
            configFile.renameTo(new File(getDataFolder(), "config.yml"));
            reloadConfig();
        } else {
            try {
                Reader defConfigStream = new InputStreamReader(getResource("bukkitconfig.yml"), "UTF-8");
                YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
                getConfig().setDefaults(defConfig);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }

        }
    }
}
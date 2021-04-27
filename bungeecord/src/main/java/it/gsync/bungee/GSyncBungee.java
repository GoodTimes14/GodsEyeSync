package it.gsync.bungee;

import it.gsync.bungee.commands.impl.GSyncAlertsCommand;
import it.gsync.bungee.commands.impl.GSyncVerboseCommand;
import it.gsync.bungee.commands.impl.GsyncLogsCommand;
import it.gsync.bungee.config.ConfigHandler;
import it.gsync.bungee.data.manager.DataManager;
import it.gsync.bungee.listeners.impl.PlayerConnection;
import it.gsync.bungee.listeners.impl.PluginListener;
import it.gsync.common.data.DataConnector;
import it.gsync.common.data.impl.H2Connector;
import it.gsync.common.data.impl.HikariConnector;
import it.gsync.common.data.impl.MongoConnector;
import it.gsync.common.data.types.ConnectionDetails;
import it.gsync.common.data.types.StorageType;
import it.gsync.common.messages.MessageHandler;
import it.gsync.common.utils.FileUtils;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;

@Getter
@Setter
public class GSyncBungee extends Plugin {


    private DataManager dataManager;
    private Map<StorageType,Supplier<DataConnector>> connectorsMap;
    private ConnectionDetails connectionDetails;
    private ConfigHandler configHandler;
    private MessageHandler messageHandler;
    private DataConnector dataConnector;
    public Configuration config;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        try {
            InputStream in = getResourceAsStream("bungeeconfig.yml");
            Configuration defaults = ConfigurationProvider.getProvider(YamlConfiguration.class).load(in);
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(new File(getDataFolder(), "config.yml"),defaults);
        } catch (IOException e) {
            e.printStackTrace();
        }
        configHandler = new ConfigHandler(this);
        dataManager = new DataManager(this);
        connectorsMap = new HashMap<>();
        connectorsMap.put(StorageType.MONGO,() -> new MongoConnector(getLogger(),connectionDetails));
        connectorsMap.put(StorageType.MYSQL,() -> new HikariConnector(getLogger(),connectionDetails));
        connectorsMap.put(StorageType.H2,() -> new H2Connector(getLogger(),connectionDetails));
        if(config.getBoolean("storage.enabled")) {
            StorageType storageType = StorageType.valueOf(config.getString("storage.type").toUpperCase(Locale.ROOT));
            String host = config.getString("storage.host");
            int port = config.getInt("storage.port");
            String database = config.getString("storage.database");
            boolean auth = config.getBoolean("storage.auth");
            String username = config.getString("storage.credentials.username");
            String password = config.getString("storage.credentials.password");
            connectionDetails = new ConnectionDetails(storageType,host,port,database,auth,username,password,getDataFolder(),(String) getConfigHandler().getSetting("h2_dbname"));
            getLogger().log(Level.INFO,"Checking if drivers are downloaded...");
            FileUtils.downloadLibraries(new File(getDataFolder(),"libraries"),connectionDetails);
            dataConnector = connectorsMap.get(storageType).get();
        }
        messageHandler = new MessageHandler(dataManager,configHandler,dataConnector);
        registerChannels();
        registerListeners();
        registerCommands();
        getLogger().fine("§aGodsEyeSync Enabled (version: "  + getDescription().getVersion() + ")");
    }

    private void registerListeners() {
        new PlayerConnection(this);
        new PluginListener(this);
    }

    private void registerChannels() {
        getLogger().info("Register channels...");
        getProxy().getInstance().registerChannel("gsync:alerts");
        getProxy().getInstance().registerChannel("gsync:violations");
        getProxy().getInstance().registerChannel("gsync:punishments");
    }

    private void registerCommands() {
        new GSyncAlertsCommand(this,"bungeealerts",config.getString("settings.permission_alert"),"gsyncalerts");
        new GsyncLogsCommand(this,"bungeelogs",config.getString("settings.permission_logs"),"gsynclogs");
        new GSyncVerboseCommand(this,"bungeeverbose",config.getString("settings.permission_verbose"),"gsyncverbose");
    }


    private void saveDefaultConfig() {
        if (!getDataFolder().exists())
            getDataFolder().mkdir();
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try (InputStream in = getResourceAsStream("bungeeconfig.yml")) {
                Files.copy(in, configFile.toPath());
                configFile.renameTo(new File(getDataFolder(), "config.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}

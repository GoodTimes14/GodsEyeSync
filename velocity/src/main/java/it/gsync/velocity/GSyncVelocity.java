package it.gsync.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import it.gsync.common.data.DataConnector;
import it.gsync.common.data.impl.H2Connector;
import it.gsync.common.data.impl.HikariConnector;
import it.gsync.common.data.impl.MongoConnector;
import it.gsync.common.data.types.ConnectionDetails;
import it.gsync.common.data.types.StorageType;
import it.gsync.common.dependencies.manager.DependencyManager;
import it.gsync.common.messages.MessageHandler;
import it.gsync.velocity.appender.VelocityClassPathAppender;
import it.gsync.velocity.commands.impl.AlertsCommand;
import it.gsync.velocity.commands.impl.LogsCommand;
import it.gsync.velocity.commands.impl.VerbosesCommand;
import it.gsync.velocity.config.ConfigHandler;
import it.gsync.velocity.data.manager.DataManager;
import it.gsync.velocity.listeners.impl.PlayerConnection;
import it.gsync.velocity.listeners.impl.PluginListener;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.logging.Logger;


@Plugin(id = "godseyesync", name = "GodsEyeSync", version = "2.2.0",
        description = "GodsEyeSync plugin", authors = {"GoodTimes14"})
@Getter
@Setter
public class GSyncVelocity {

    private final ProxyServer server;
    private final Logger logger;
    private MessageHandler messageHandler;
    private final File dataDirectory;
    private ConnectionDetails connectionDetails;
    private DependencyManager dependencyManager;
    private DataManager dataManager;
    private DataConnector dataConnector;
    private Map<StorageType, Supplier<DataConnector>> connectorsMap;
    private ConfigHandler configHandler;

    @Inject
    public GSyncVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = Logger.getLogger("GodsEyeSync");
        this.dataDirectory = dataDirectory.toFile();
        if(!this.dataDirectory.exists()) {
            this.dataDirectory.mkdir();
        }
    }

    @Subscribe
    public void onInit(ProxyInitializeEvent event) {
        server.getChannelRegistrar().register(MinecraftChannelIdentifier.create("gsync","alerts"));
        server.getChannelRegistrar().register(MinecraftChannelIdentifier.create("gsync","violations"));
        server.getChannelRegistrar().register(MinecraftChannelIdentifier.create("gsync","punishments"));
        connectorsMap = new HashMap<>();
        connectorsMap.put(StorageType.MONGO,() -> new MongoConnector(getLogger(),connectionDetails));
        connectorsMap.put(StorageType.MYSQL,() -> new HikariConnector(getLogger(),connectionDetails));
        connectorsMap.put(StorageType.H2,() -> new H2Connector(getLogger(),connectionDetails));
        configHandler = new ConfigHandler(this);
        dependencyManager = new DependencyManager(new File(getDataDirectory(),"libraries"),new VelocityClassPathAppender(this,server));
        dataManager = new DataManager(this);
        if(configHandler.getConfiguration().get("storage.enabled",Boolean.class)) {
            StorageType storageType = StorageType.valueOf(configHandler.getConfiguration().get("storage.type",String.class).toUpperCase(Locale.ROOT));
            String host = configHandler.getConfiguration().get("storage.host",String.class);
            int port = configHandler.getConfiguration().get("storage.port",int.class);
            String database = configHandler.getConfiguration().get("storage.database",String.class);
            boolean auth = configHandler.getConfiguration().get("storage.auth",Boolean.class);
            String username = configHandler.getConfiguration().get("storage.credentials.username",String.class);
            String password = configHandler.getConfiguration().get("storage.credentials.password",String.class);
            connectionDetails = new ConnectionDetails(storageType,host,port,database,auth,username,password,getDataDirectory(),(String) getConfigHandler().getSetting("h2_dbname"));
            getLogger().log(Level.INFO,"Checking if drivers are downloaded...");
            dependencyManager.loadDependencies(connectionDetails.getStorageType());
            dataConnector = connectorsMap.get(storageType).get();
        }
        registerListeners();
        registerCommands();
        messageHandler = new MessageHandler(dataManager,configHandler,dataConnector);
    }

    public void registerListeners() {
        new PlayerConnection(this);
        new PluginListener(this);
    }

    public void registerCommands() {
        new AlertsCommand(this,"gsyncalerts", (String) configHandler.getSetting("permission_alert"),"bungeealerts");
        new LogsCommand(this,"gsynclogs", (String) configHandler.getSetting("permission_logs"),"bungeelogs");
        new VerbosesCommand(this,"gsyncverbose", (String) configHandler.getSetting("permission_verbose"),"bungeeverbose");
    }



}

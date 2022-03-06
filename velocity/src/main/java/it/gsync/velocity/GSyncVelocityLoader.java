package it.gsync.velocity;

import com.google.inject.Inject;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import it.gsync.common.classloader.JarInJarClassLoader;
import it.gsync.velocity.appender.VelocityClassPathAppender;
import it.gsync.velocity.plugin.GSyncVelocity;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.logging.Logger;


@Plugin(id = "godseyesync", name = "GodsEyeSync", version = "2.2.0",
        description = "GodsEyeSync plugin", authors = {"GoodTimes14"})
@Getter
@Setter
public class GSyncVelocityLoader {

    private final ProxyServer server;
    private final Logger logger;
    private final File dataFolder;
    private GSyncVelocity plugin;

    @Inject
    public GSyncVelocityLoader(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataFolder = dataDirectory.toFile();

    }

    @Subscribe
    public void onInit(ProxyInitializeEvent event) {
        VelocityClassPathAppender classPathAppender = new VelocityClassPathAppender(this,server);
        File file = JarInJarClassLoader.copyFile(getClass().getClassLoader(), "gsyncdatabase.jarinjar",dataFolder);
        classPathAppender.addJarToClasspath(file.toPath());
        plugin = new GSyncVelocity(this,server,logger,dataFolder,classPathAppender);
        plugin.onEnable();
    }




}

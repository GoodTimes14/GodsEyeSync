package it.gsync.velocity.appender;

import com.velocitypowered.api.proxy.ProxyServer;
import it.gsync.common.classloader.ClassPathAppender;
import it.gsync.velocity.GSyncVelocityLoader;

import java.io.IOException;
import java.nio.file.Path;

public class VelocityClassPathAppender implements ClassPathAppender {


    private GSyncVelocityLoader plugin;
    private ProxyServer server;

    public VelocityClassPathAppender(GSyncVelocityLoader plugin, ProxyServer server) {
        this.plugin = plugin;
        this.server = server;
    }

    @Override
    public void addJarToClasspath(Path file) {
        server.getPluginManager().addToClasspath(plugin,file);
    }

    @Override
    public void close() throws IOException {

    }
}

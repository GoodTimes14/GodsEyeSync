package it.gsync.velocity.appender;

import com.velocitypowered.api.proxy.ProxyServer;
import it.gsync.common.classloader.ClassPathAppender;
import it.gsync.velocity.GSyncVelocity;

import java.io.IOException;
import java.nio.file.Path;

public class VelocityClassPathAppender implements ClassPathAppender {


    private GSyncVelocity plugin;
    private ProxyServer server;

    public VelocityClassPathAppender(GSyncVelocity plugin,ProxyServer server) {
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

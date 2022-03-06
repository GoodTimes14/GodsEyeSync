package it.gsync.bungeeloader;

import it.gsync.common.GSync;
import it.gsync.common.classloader.JarInJarClassLoader;
import net.md_5.bungee.api.plugin.Plugin;

public class GSyncBungeeLoader extends Plugin {


    private static final String PLUGIN_JAR_NAME = "gsyncbungee.jarinjar";
    private static final String DATA_JAR_NAME = "gsyncdatabase.jarinjar";
    private static final String GSYNC_CLASS = "it.gsync.bungee.GSyncBungee";

    private final GSync gSyncPlugin;
    private final JarInJarClassLoader classLoader;

    public GSyncBungeeLoader() {
        classLoader = new JarInJarClassLoader(getClass().getClassLoader(),PLUGIN_JAR_NAME);
        classLoader.addJarToClasspath(JarInJarClassLoader.extractJar(getClass().getClassLoader(),DATA_JAR_NAME));
        gSyncPlugin = classLoader.instantiatePlugin(GSYNC_CLASS,Plugin.class,this);
    }

    @Override
    public void onLoad() {
        gSyncPlugin.onLoad();
    }

    @Override
    public void onEnable() {
        gSyncPlugin.onEnable();
    }

    @Override
    public void onDisable() {
        gSyncPlugin.onDisable();
    }
}

package it.gsync.bungeeloader;

import it.gsync.common.GSync;
import it.gsync.common.classloader.JarInJarClassLoader;
import net.md_5.bungee.api.plugin.Plugin;

public class GSyncBungeeLoader extends Plugin {


    private static final String JAR_NAME = "gsyncbungee.jarinjar";
    private static final String GSYNC_CLASS = "it.gsync.bungee.GSyncBungee";

    private final GSync gSyncPlugin;
    private final JarInJarClassLoader classLoader;

    public GSyncBungeeLoader() {
        classLoader = new JarInJarClassLoader(getClass().getClassLoader(),JAR_NAME);
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

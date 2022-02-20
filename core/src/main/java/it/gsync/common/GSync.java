package it.gsync.common;

import it.gsync.common.classloader.ClassPathAppender;

import java.io.File;
import java.util.logging.Logger;

public interface GSync {



    void onEnable();

    void onDisable();

    ClassPathAppender getClassPathAppender();

    void onLoad();

    Logger getLogger();

    File getDataFolder();
}

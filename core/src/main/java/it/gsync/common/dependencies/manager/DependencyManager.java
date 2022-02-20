package it.gsync.common.dependencies.manager;

import it.gsync.common.classloader.ClassPathAppender;
import it.gsync.common.data.types.StorageType;
import it.gsync.common.dependencies.Dependency;
import it.gsync.common.utils.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.List;

public class DependencyManager {

    private final File dataFolder;
    public ClassPathAppender classPathAppender;

    public DependencyManager(File dataFolder, ClassPathAppender classPathAppender) {
        if(!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        System.out.println("classLoader: " + getClass().getClassLoader().getClass().getSimpleName());
        this.classPathAppender = classPathAppender;
        this.dataFolder = dataFolder;
    }


    public void loadDependencies(StorageType type) {
        for(Dependency dependency : type.getDependencies()) {
            File file = loadDependency(dependency);
            if(file != null) {
                classPathAppender.addJarToClasspath(file.toPath());
            }
        }
    }

    public File loadDependency(Dependency dependency) {
        if(isClassLoaded(dependency.getCheckClass())) return null;
        File file = new File(dataFolder + File.separator,dependency.getFileName());
        if(!file.exists()) {
            download(dependency,file);
        }
        return file;
    }

    public void download(Dependency dependency,File file) {
        try {
            InputStream stream = dependency.toMavenURL().openStream();
            ReadableByteChannel channel = Channels.newChannel(stream);
            FileOutputStream out = new FileOutputStream(file);
            out.getChannel().transferFrom(channel, 0L, Long.MAX_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }


    private boolean isClassLoaded(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}

package it.gsync.common.classloader.impl;

import it.gsync.common.classloader.ClassPathAppender;
import it.gsync.common.classloader.JarInJarClassLoader;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;

public class DefaultClassPathAppender implements ClassPathAppender {

    private JarInJarClassLoader classLoader;

    public DefaultClassPathAppender(JarInJarClassLoader classLoader) {
        this.classLoader = classLoader;
    }

    @Override
    public void addJarToClasspath(Path file) {
        try {
            classLoader.addJarToClasspath(file.toUri().toURL());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() throws IOException {
        classLoader.deleteJarResource();
        classLoader.close();
    }
}

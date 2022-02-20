package it.gsync.common.classloader;

import it.gsync.common.GSync;
import it.gsync.common.utils.loader.ClassLoaderException;

import javax.management.ReflectionException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

public class JarInJarClassLoader extends URLClassLoader {


    static {
        ClassLoader.registerAsParallelCapable();
    }


    public JarInJarClassLoader(ClassLoader loaderClassLoader, String jarResourcePath) {
        super(new URL[]{extractJar(loaderClassLoader, jarResourcePath)}, loaderClassLoader);
    }

    public void addJarToClasspath(URL url) {
        addURL(url);
    }

    public void deleteJarResource() {
        URL[] urls = getURLs();
        if (urls.length == 0) {
            return;
        }

        try {
            Path path = Paths.get(urls[0].toURI());
            Files.deleteIfExists(path);
        } catch (Exception e) {
            // ignore
        }
    }

    public <T> GSync instantiatePlugin(String bootstrapClass, Class<T> loaderPluginType, T loaderPlugin) {
        Class<? extends GSync> plugin;
        try {
            plugin = loadClass(bootstrapClass).asSubclass(GSync.class);
        } catch (ReflectiveOperationException e) {
            throw new ClassLoaderException("Unable to load class");
        }

        Constructor<? extends GSync> constructor;
        try {
            constructor = plugin.getConstructor(loaderPluginType);
        } catch (ReflectiveOperationException e) {
            throw new ClassLoaderException("Unable to get the constructor");

        }

        try {
            return constructor.newInstance(loaderPlugin);
        } catch (ReflectiveOperationException e) {
            throw new ClassLoaderException("Unable to get the constructor");
        }
    }

    private static URL extractJar(ClassLoader loaderClassLoader, String jarResourcePath) throws ClassLoaderException {
        // get the jar-in-jar resource
        URL jarInJar = loaderClassLoader.getResource(jarResourcePath);
        if (jarInJar == null) {
            throw new ClassLoaderException("Could not locate jar-in-jar");
        }

        // create a temporary file
        // on posix systems by default this is only read/writable by the process owner
        Path path;
        try {
            path = Files.createTempFile("gsync-jarinjar", ".jar.tmp");
        } catch (IOException e) {
            throw new ClassLoaderException("Unable to create a temporary file", e);
        }

        // mark that the file should be deleted on exit
        path.toFile().deleteOnExit();

        // copy the jar-in-jar to the temporary file path
        try (InputStream in = jarInJar.openStream()) {
            Files.copy(in, path, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ClassLoaderException("Unable to copy jar-in-jar to temporary path", e);
        }

        try {
            return path.toUri().toURL();
        } catch (MalformedURLException e) {
            throw new ClassLoaderException("Unable to get URL from path", e);
        }
    }

}

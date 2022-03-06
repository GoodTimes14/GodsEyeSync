package it.gsync.common.utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;


public class FileUtils {

    private static ClassLoader injectorClassLoader = FileUtils.class.getClassLoader();


    //method from DeprecatedLuke
    public static void download(File file, String from){
        try {
            URL url = new URL(from);
            InputStream stream = url.openStream();
            ReadableByteChannel channel = Channels.newChannel(stream);
            FileOutputStream out = new FileOutputStream(file);
            out.getChannel().transferFrom(channel, 0L, Long.MAX_VALUE);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    //method from DeprecatedLuke
    public static void injectURL(ClassLoader loader,URL url) {
        try {
            URLClassLoader systemClassLoader = (URLClassLoader) injectorClassLoader;
            Class<URLClassLoader> classLoaderClass = URLClassLoader.class;
            try {
                Method method = classLoaderClass.getDeclaredMethod("addURL", URL.class);
                method.setAccessible(true);
                method.invoke(systemClassLoader, url);
            } catch (Throwable t) {
                t.printStackTrace();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static boolean isClassLoaded(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}

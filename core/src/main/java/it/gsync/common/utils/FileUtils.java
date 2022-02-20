package it.gsync.common.utils;

import it.gsync.common.data.types.ConnectionDetails;
import org.slf4j.LoggerFactory;

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

    public static void downloadLibraries(File dataFolder, ConnectionDetails connectionDetails) {
        if(!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        switch (connectionDetails.getStorageType()) {
            case MYSQL:
                File loggerFile = new File(dataFolder + File.separator,"slf4j.jar");
                File hikariFile = new File(dataFolder + File.separator, "hikaricp.jar");
                File jdbcFile = new File(dataFolder + File.separator, "jdbc.jar");
                checkOrDownload(jdbcFile,"com.mysql.jdbc.Connection","https://repo1.maven.org/maven2/mysql/mysql-connector-java/8.0.24/mysql-connector-java-8.0.24.jar");
                checkOrDownload(loggerFile,"org.slf4j.LoggerFactory","https://repo1.maven.org/maven2/org/slf4j/slf4j-api/1.7.30/slf4j-api-1.7.30.jar");
                checkOrDownload(hikariFile,"com.zaxxer.hikari.HikariDataSource","https://repo1.maven.org/maven2/com/zaxxer/HikariCP/4.0.3/HikariCP-4.0.3.jar");
                break;
            case MONGO:
                File mongoFile = new File(dataFolder + File.separator, "mongodriver.jar");
                checkOrDownload(mongoFile,"org.mongodb.MongoClient","https://repo1.maven.org/maven2/org/mongodb/mongo-java-driver/3.12.8/mongo-java-driver-3.12.8.jar");
                break;
            case H2:
                File h2File = new File(dataFolder + File.separator, "h2driver.jar");
                checkOrDownload(h2File,"org.h2.Driver", "https://repo1.maven.org/maven2/com/h2database/h2/1.4.200/h2-1.4.200.jar");
                break;
        }
    }


    private static void checkOrDownload(File file,String mainClass,String url) {
        try {
            if(isClassLoaded(mainClass)) {
                return;
            }
            if (!file.exists()) {
                FileUtils.download(file, url);
            }
            //FileUtils.injectURL(file.toURI().toURL());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

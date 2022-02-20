package it.gsync.common.classloader;

import java.io.Closeable;
import java.nio.file.Path;

public interface ClassPathAppender extends Closeable {

    void addJarToClasspath(Path file);



}

package it.gsync.common.utils.loader;

public class ClassLoaderException extends RuntimeException {


    public ClassLoaderException(String s) {
        super(s);
    }


    public ClassLoaderException(String s,Throwable throwable) {
        super(s,throwable);
    }

}

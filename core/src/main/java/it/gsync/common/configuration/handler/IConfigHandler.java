package it.gsync.common.configuration.handler;

public interface IConfigHandler {


    String getMessage(String path);

    Object getSetting(String path);

    String getBanMessage();
}

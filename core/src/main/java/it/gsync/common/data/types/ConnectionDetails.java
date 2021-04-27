package it.gsync.common.data.types;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

@Getter
@Setter
@AllArgsConstructor
public class ConnectionDetails {


    private StorageType storageType;
    private String host;
    private int port;
    private String database;
    private boolean auth;
    private String username;
    private String password;
    private File dataFolder;
    private String fileName;

}

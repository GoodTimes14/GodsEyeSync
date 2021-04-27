package it.gsync.common.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.UUID;

@Getter
@AllArgsConstructor
public class Alert {

    private UUID uuid;
    private String playerName;
    private String server;
    private String checkType;
    private int vl;

}

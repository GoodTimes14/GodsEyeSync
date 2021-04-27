package it.gsync.common.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Flag {

    private UUID uuid;
    private String playerName;
    private String server;
    private String detection;
    private String checkType;
    private int vl;
    private int ping;
    private double tps;
    private long timestamp;

    public Flag(){}
}

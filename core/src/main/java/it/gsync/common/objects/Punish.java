package it.gsync.common.objects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class Punish {

    private UUID uuid;
    private String playerName;
    private String server;
    private String checkType;
    private String punishType;
    private String date;

    public Punish(){}

}

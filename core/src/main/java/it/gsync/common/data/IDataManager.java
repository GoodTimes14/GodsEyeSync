package it.gsync.common.data;

import it.gsync.common.objects.Alert;
import it.gsync.common.objects.Flag;
import it.gsync.common.objects.Punish;

public interface IDataManager {


    void sendAlertMessage(Alert alert);

    void sendFlagMessage(Flag flag);

    void sendPunishMessage(Punish punish);

    void executePunish(Punish punish);

}


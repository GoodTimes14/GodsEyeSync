package it.gsync.data.messages;

import it.gsync.common.configuration.handler.IConfigHandler;
import it.gsync.common.data.IDataManager;
import it.gsync.common.objects.Alert;
import it.gsync.common.objects.Flag;
import it.gsync.common.objects.Punish;
import it.gsync.data.DataConnector;
import lombok.Getter;

@Getter
public class MessageHandler {


    private IDataManager dataManager;
    private DataConnector dataConnector;
    private IConfigHandler configHandler;

    public MessageHandler(IDataManager dataManager,IConfigHandler configHandler,DataConnector dataConnector) {
        this.dataManager = dataManager;
        this.configHandler = configHandler;
        this.dataConnector = dataConnector;
    }

    public void handleMessage(Object message) {
        if(message instanceof Alert) {
            dataManager.sendAlertMessage((Alert) message);
        } else if(message instanceof Flag) {
            dataManager.sendFlagMessage((Flag) message);
        } else if(message instanceof Punish) {
            Punish punish = (Punish) message;
            dataManager.sendPunishMessage(punish);
            dataManager.executePunish(punish);
        }
        if(!(message instanceof Alert) && dataConnector != null) {
            dataConnector.save(message);
        }
    }

}

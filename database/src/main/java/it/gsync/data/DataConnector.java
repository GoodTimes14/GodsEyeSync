package it.gsync.data;

import it.gsync.data.types.ConnectionDetails;
import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

@Getter
@Setter
public abstract class DataConnector {

    private Logger logger;
    private ConnectionDetails details;
    private ExecutorService executorService;
    private LinkedBlockingQueue<Object> saveQueue;

    protected DataConnector(Logger logger, ConnectionDetails details) {
        this.logger = logger;
        this.details = details;
        saveQueue = new LinkedBlockingQueue<>();
        executorService = Executors.newSingleThreadExecutor();
        executorService.submit(this::checkSaveQueue);
        connect();
    }

    public abstract void connect();

    public abstract <T> T fetchlastObject(Class<T> type,String field,Object value);

    public abstract <T> List<T> fetchObjects(Class<T> type, String field, Object value);

    public void checkSaveQueue() {
        while (true) {
            try {
                Object obj = saveQueue.take();
                saveObject(obj);
            } catch (Exception e) {
                logger.log(Level.SEVERE,"Exception while interrupting thread: ",e);
            }
        }
    }

    public void save(Object object) {
        saveQueue.add(object);
    }

    public abstract void saveObject(Object object);

}

package it.gsync.data.impl;

import com.google.common.collect.Lists;
import com.mongodb.*;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import it.gsync.common.objects.Flag;
import it.gsync.common.objects.Punish;
import it.gsync.data.DataConnector;
import it.gsync.data.types.ConnectionDetails;
import lombok.Getter;
import org.bson.codecs.configuration.CodecRegistry;
import org.bson.codecs.pojo.PojoCodecProvider;

import java.util.List;
import java.util.logging.Logger;

import static org.bson.codecs.configuration.CodecRegistries.fromProviders;
import static org.bson.codecs.configuration.CodecRegistries.fromRegistries;

@Getter
public class MongoConnector extends DataConnector {


    private MongoClient mongoClient;
    private MongoCollection<Flag> flagsCollection;
    private MongoCollection<Punish> punishCollection;

    public MongoConnector(Logger logger, ConnectionDetails details) {
        super(logger, details);
    }


    @Override
    public void connect() {
        CodecRegistry pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(),
                fromProviders(PojoCodecProvider.builder().automatic(true).build()));
        if(getDetails().isAuth()) {
            MongoCredential credential = MongoCredential.createCredential(getDetails().getUsername(), getDetails().getDatabase(),getDetails().getPassword().toCharArray());
            this.mongoClient = new MongoClient(new ServerAddress(getDetails().getHost(),getDetails().getPort()), credential,MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build());
        } else {
            this.mongoClient = new MongoClient(new ServerAddress(getDetails().getHost(),getDetails().getPort()),MongoClientOptions.builder().codecRegistry(pojoCodecRegistry).build());
        }
        flagsCollection = mongoClient.getDatabase(getDetails().getDatabase()).getCollection("flags", Flag.class);
        punishCollection = mongoClient.getDatabase(getDetails().getDatabase()).getCollection("punish", Punish.class);
    }

    @Override
    public <T> T fetchlastObject(Class<T> type, String field, Object value) {
        if(flagsCollection.getDocumentClass() == type) {
            return (T) flagsCollection.find(Filters.eq(field,value)).sort(new BasicDBObject("_id",-1)).first();
        } else if(punishCollection.getDocumentClass() == type) {
            return (T) punishCollection.find(Filters.eq(field,value)).sort(new BasicDBObject("_id",-1)).first();
        }
        throw new IllegalArgumentException("Type not found");
    }

    @Override
    public <T> List<T> fetchObjects(Class<T> type, String field, Object value) {
        if(flagsCollection.getDocumentClass() == type) {
            List<Flag> flags = Lists.newArrayList();
            for(Flag flag :  flagsCollection.find(Filters.eq(field,value))) {
                flags.add(flag);
            }
            return (List<T>) flags;
        } else if(punishCollection.getDocumentClass() == type) {
            List<Punish> punishes = Lists.newArrayList();
            for(Punish punish :  punishCollection.find(Filters.eq(field,value))) {
                punishes.add(punish);
            }
            return (List<T>) punishes;
        }
        throw new IllegalArgumentException("Type not found");
    }

    @Override
    public void saveObject(Object object) {
        if(object instanceof Flag) {
            flagsCollection.insertOne((Flag) object);
        } else if(object instanceof Punish) {
            punishCollection.insertOne((Punish) object);
        }
    }
}

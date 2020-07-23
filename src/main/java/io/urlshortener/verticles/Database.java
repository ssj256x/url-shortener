package io.urlshortener.verticles;

import io.urlshortener.dao.URLRepository;
import io.urlshortener.dao.URLRepositoryImpl;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.serviceproxy.ServiceBinder;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class Database extends AbstractVerticle {

    @Override
    public void start() {
        JsonObject dsConfig = config().getJsonObject("datasource");
        JsonObject dbProp = new JsonObject();
//        dbProp.put("host", dsConfig.getValue("host"));
//        dbProp.put("port", dsConfig.getValue("port"));
//        dbProp.put("db_name", dsConfig.getValue("db_name"));
        dbProp.put("connection_string", dsConfig.getValue("connection_string"));
        final MongoClient mongoClient = MongoClient.createShared(vertx, dbProp);
        final URLRepository urlRepository = new URLRepositoryImpl(mongoClient);

        new ServiceBinder(vertx)
                .setAddress(URLRepository.SERVICE_ADDRESS)
                .register(URLRepository.class, urlRepository);
    }
}

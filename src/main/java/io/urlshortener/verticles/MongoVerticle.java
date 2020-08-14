package io.urlshortener.verticles;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

@Log4j2
public class MongoVerticle extends AbstractVerticle {

    private MongoClient mongoClient;
    private final JsonObject config;

    public MongoVerticle(JsonObject config) {
        this.config = config;
    }

    @Override
    public void start(Promise<Void> promise) {
//        vertx.executeBlocking(future -> {
//
//                },
//                ar -> {
//                    if (ar.succeeded()) LOGGER.info("DB Initialized");
//                    else LOGGER.error("Error while init DB : {}", ar.cause().getMessage());
//                });

        createMongoClient().onComplete(promise);
    }

    private Future<Void> createMongoClient() {

        Promise<Void> promise = Promise.promise();

        LOGGER.info("Initializing DB...");

        JsonObject dsConfig = config.getJsonObject("datasource");
        JsonObject dbProp = new JsonObject();
        String mdbConnStr = dsConfig.getString("connection_string");

        if (StringUtils.isBlank(mdbConnStr)) {
            dbProp.put("host", dsConfig.getValue("host"));
            dbProp.put("port", dsConfig.getValue("port"));
            dbProp.put("db_name", dsConfig.getValue("db_name"));
        } else {
            dbProp.put("connection_string", mdbConnStr);
        }

        LOGGER.debug("Loaded DB Properties : {}", dbProp);

        try {
            mongoClient = MongoClient.createShared(vertx, dbProp);
            config.put("mongo.client", mongoClient);
        } catch (Exception e) {
            LOGGER.error("Error while init DB");
            LOGGER.error(e.getStackTrace(), e);
            promise.fail(e.getCause());
        }
        return promise.future();
    }
}

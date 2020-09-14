package io.urlshortener.verticles;

import io.urlshortener.dao.URLRepository;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import io.vertx.serviceproxy.ServiceBinder;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

/**
 * Verticle to connect to DB
 */
@Log4j2
public class URLRepositoryVerticle extends AbstractVerticle {

    private MongoClient mongoClient;

    @Override
    public void start(Promise<Void> promise) {

        vertx.executeBlocking(future -> {
                    LOGGER.info("Initializing DB...");

                    JsonObject dsConfig = config().getJsonObject("datasource");
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
                        LOGGER.info("mongoClient in constructor : " + mongoClient);
                        future.complete();
                    } catch (Exception e) {
                        LOGGER.error("Error while init DB");
                        future.fail(e.getCause());
                    }
                },
                ar -> {

                    if (ar.failed()) {
                        LOGGER.error("Error while init DB : {}", ar.cause().getMessage());
                        promise.fail(ar.cause());
                    } else {
                        promise.complete();
                    }
                });

        promise.future().onSuccess(ar -> {

            new ServiceBinder(vertx)
                    .setAddress(URLRepository.SERVICE_ADDRESS)
                    .register(URLRepository.class, URLRepository.create(mongoClient));

            LOGGER.info("DB Initialized");
        });
    }
}

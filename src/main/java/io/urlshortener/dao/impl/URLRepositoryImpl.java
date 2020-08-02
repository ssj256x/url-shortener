package io.urlshortener.dao.impl;

import io.urlshortener.dao.URLRepository;
import io.urlshortener.model.URLData;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation class for {@link URLRepository} interface
 */
@Log4j2
public class URLRepositoryImpl implements URLRepository {

    private MongoClient mongoClient;

    /**
     * Constructor used to initialize a DB Client by connecting with a server
     *
     * @param vertx  - The vertx instance
     * @param config - The configuration object
     */
    public URLRepositoryImpl(Vertx vertx, JsonObject config) {
        vertx.executeBlocking(future -> {
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
                    } catch (Exception e) {
                        LOGGER.error("Error while init DB");
                    }
                },
                ar -> {
                    if (ar.succeeded()) LOGGER.info("DB Initialized");
                    else LOGGER.error("Error while init DB : {}", ar.cause().getMessage());
                });
    }

    @Override
    public URLRepository save(URLData urlData, Handler<AsyncResult<String>> resultHandler) {

        mongoClient.save(URLData.DB_COLLECTION,
                urlData.toJson(),
                ar -> {
                    if (ar.failed()) {
                        LOGGER.error("Error occurred while saving data");
                        resultHandler.handle(Future.failedFuture(ar.cause()));
                        return;
                    }
                    LOGGER.info("After saving {}", ar.result());
                    resultHandler.handle(Future.succeededFuture(urlData.getUrlId()));
                });

        return this;
    }

    @Override
    public URLRepository findAll(Handler<AsyncResult<List<URLData>>> resultHandler) {

        mongoClient.find(URLData.DB_COLLECTION,
                new JsonObject(),
                ar -> {
                    if (ar.failed()) {
                        LOGGER.error("Error occurred while fething data");
                        resultHandler.handle(Future.failedFuture(ar.cause()));
                        return;
                    }
                    // TODO : Try to use reflection to make a generic object mapper
                    List<URLData> urlDataList = ar.result()
                            .stream()
                            .map(data -> new URLData(
                                    data.getString("urlId"),
                                    data.getString("url"),
                                    data.getString("user"),
                                    data.getString("createdOn")
                            ))
                            .collect(Collectors.toList());

                    resultHandler.handle(Future.succeededFuture(urlDataList));
                });

        return this;
    }

    @Override
    public URLRepository findById(String id, Handler<AsyncResult<URLData>> resultHandler) {

        mongoClient.find(URLData.DB_COLLECTION,
                new JsonObject().put("urlId", id),
                ar -> {
                    if (ar.failed()) {
                        LOGGER.error("Error occurred while fetching data");
                        resultHandler.handle(Future.failedFuture(ar.cause()));
                        return;
                    }
                    LOGGER.info("ar.result() : {}", ar.result());
                    List<URLData> urlDataList = ar.result()
                            .stream()
                            .map(data -> new URLData(
                                    data.getString("urlId"),
                                    data.getString("url"),
                                    data.getString("user"),
                                    data.getString("createdOn")
                            ))
                            .collect(Collectors.toList());

                    URLData urlData = !urlDataList.isEmpty() ? urlDataList.get(0) : null;

                    resultHandler.handle(Future.succeededFuture(urlData));
                });

        return this;
    }

    @Override
    public URLRepository findByURL(String url, Handler<AsyncResult<List<URLData>>> resultHandler) {
        return null;
    }

    @Override
    public URLRepository findByUser(String user, Handler<AsyncResult<List<URLData>>> resultHandler) {
        return null;
    }
}

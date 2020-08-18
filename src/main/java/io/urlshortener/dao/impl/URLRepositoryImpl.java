package io.urlshortener.dao.impl;

import io.urlshortener.dao.URLRepository;
import io.urlshortener.helper.RepositoryHelper;
import io.urlshortener.model.URLData;
import io.urlshortener.model.URLDataConverter;
import io.vertx.core.*;
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
    private RepositoryHelper repositoryHelper;

    /**
     * Constructor used to initialize a DB Client by connecting with a server
     *
     * @param vertx  - The vertx instance
     * @param config - The configuration object
     */
    public URLRepositoryImpl(Vertx vertx, JsonObject config) {

        // Initialising DB by creating a MongoClient
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
                        LOGGER.info("mongoClient in constructor : " + mongoClient);
                        future.complete();
                    } catch (Exception e) {
                        LOGGER.error("Error while init DB");
                        future.fail(e.getCause());
                    }
                },
                ar -> {
                    if (ar.succeeded()) {
                        LOGGER.info("DB Initialized");
                        repositoryHelper = new RepositoryHelper(mongoClient, URLData.DB_COLLECTION);
                        LOGGER.info("repositoryHelper : " + repositoryHelper);
                    } else {
                        LOGGER.error("Error while init DB : {}", ar.cause().getMessage());
                    }
                });

        LOGGER.info("repositoryHelper : " + repositoryHelper);
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
                        LOGGER.error("Error occurred while fetching data");
                        return;
                    }
                    List<JsonObject> result = ar.result();
                    LOGGER.info("result optional : " + result);

                    resultHandler.handle(Future.succeededFuture(
                            result.stream()
                                    .map(repositoryHelper::mapToURLData)
                                    .collect(Collectors.toList())
                            )
                    );
                });

        return this;
    }

    @Override
    public URLRepository findById(String id, Handler<AsyncResult<URLData>> resultHandler) {

        mongoClient.findOne(URLData.DB_COLLECTION,
                new JsonObject().put("urlId", id),
                null,
                ar -> {
                    if (ar.failed()) {
                        LOGGER.error("Error occurred while fetching data");
                        resultHandler.handle(Future.failedFuture(ar.cause()));
                        return;
                    }
                    LOGGER.info("Fetched Collection : {}", ar.result());
                    URLData urlData = ar.result() != null ? repositoryHelper.mapToURLData(ar.result()) : null;

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

        return this;
    }

    @Override
    public URLRepository delete(String id, Handler<AsyncResult<URLData>> resultHandler) {

        mongoClient.findOneAndDelete(URLData.DB_COLLECTION,
                new JsonObject().put("id", id),
                ar -> {
                    if (ar.failed()) {
                        LOGGER.error("Error occurred while deleting data");
                        resultHandler.handle(Future.failedFuture(ar.cause()));
                        return;
                    }

                    LOGGER.info("Deleted Result : {}", ar.result());
                    URLData urlData = ar.result() != null ? repositoryHelper.mapToURLData(ar.result()) : null;
                    resultHandler.handle(Future.succeededFuture(urlData));
                });

        return this;
    }

    @Override
    public URLRepository update(URLData body, Handler<AsyncResult<URLData>> resultHandler) {

        JsonObject updatedURLData = new JsonObject();
        URLDataConverter.toJson(body, updatedURLData);

        mongoClient.findOneAndUpdate(URLData.DB_COLLECTION,
                new JsonObject().put("urlId", body.getUrlId()),
                updatedURLData,
                ar -> {
                    if (ar.failed()) {
                        LOGGER.error("Error occurred while updating data");
                        resultHandler.handle(Future.failedFuture(ar.cause()));
                        return;
                    }

                    LOGGER.info("ar.result() : {}", ar.result());
                    resultHandler.handle(Future.succeededFuture(repositoryHelper.mapToURLData(ar.result())));
                });

        return this;
    }

    /**
     * Executes the find method from {@link RepositoryHelper} and fetches the query.
     *
     * @param query - The query to execute
     * @return The List of {@link URLData}
     */
    private Future<List<URLData>> executeFindMany(JsonObject query) {

        Promise<List<URLData>> promise = Promise.promise();

//        .stream()
//                .map(repositoryHelper::mapToURLData)
//                .collect(Collectors.toList()))

//        return repositoryHelper
//                .find(query)
//                .onSuccess(list -> {
//                    Future.succeededFuture(
//                            list.stream()
//                                    .map(obj -> {
//                                        URLData urlData = new URLData();
//                                        URLDataConverter.fromJson(obj, urlData);
//                                        return urlData;
//                                    })
//                                    .collect(Collectors.toList()));
//                })
//                .onFailure(ar -> Future.failedFuture(ar.getCause()));
        return promise.future();
    }
}

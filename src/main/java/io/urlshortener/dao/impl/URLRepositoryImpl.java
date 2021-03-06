package io.urlshortener.dao.impl;

import io.urlshortener.dao.URLRepository;
import io.urlshortener.helper.RepositoryHelper;
import io.urlshortener.model.URLData;
import io.urlshortener.model.URLDataConverter;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import lombok.extern.log4j.Log4j2;

import java.util.List;

/**
 * Implementation class for {@link URLRepository} interface
 */
@Log4j2
public class URLRepositoryImpl implements URLRepository {

    private final MongoClient mongoClient;
    private final RepositoryHelper repositoryHelper;

    /**
     * Constructor used to initialize a DB Client by connecting with a server
     *
     * @param mongoClient  - MongoDB client to query DB
     */
    public URLRepositoryImpl(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
        repositoryHelper = new RepositoryHelper(mongoClient, URLData.DB_COLLECTION);
    }

    @Override
    public URLRepository save(URLData urlData, Handler<AsyncResult<String>> resultHandler) {

        LOGGER.info("Inserting Collection : {}", urlData.toJson().encodePrettily());
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

        repositoryHelper.findMultiple(new JsonObject(), resultHandler);
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

        repositoryHelper.findMultiple(new JsonObject().put("url", url), resultHandler);
        return this;
    }

    @Override
    public URLRepository findByUser(String user, Handler<AsyncResult<List<URLData>>> resultHandler) {

        repositoryHelper.findMultiple(new JsonObject().put("user", user), resultHandler);
        return this;
    }

    @Override
    public URLRepository delete(String id, Handler<AsyncResult<URLData>> resultHandler) {

        mongoClient.findOneAndDelete(URLData.DB_COLLECTION,
                new JsonObject().put("urlId", id),
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
        LOGGER.info("updatedURLData : {}", updatedURLData.encodePrettily());

        mongoClient.findOneAndUpdate(URLData.DB_COLLECTION,
                new JsonObject().put("urlId", body.getUrlId()),
                new JsonObject().put("$set", updatedURLData),
                ar -> {
                    if (ar.failed()) {
                        LOGGER.error("Error occurred while updating data");
                        LOGGER.error(ar.cause().getStackTrace(), ar.cause());
                        resultHandler.handle(Future.failedFuture(ar.cause()));
                        return;
                    }

                    LOGGER.info("ar.result() : {}", ar.result());
                    resultHandler.handle(Future.succeededFuture(repositoryHelper.mapToURLData(ar.result())));
                });

        return this;
    }
}

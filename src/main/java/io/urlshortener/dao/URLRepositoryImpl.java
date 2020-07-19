package io.urlshortener.dao;

import io.urlshortener.model.Info;
import io.urlshortener.model.URLData;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.stream.Collectors;

@Log4j2
public class URLRepositoryImpl implements URLRepository {

    private final MongoClient mongoClient;

    public URLRepositoryImpl(MongoClient mongoClient) {
        this.mongoClient = mongoClient;
    }

    @Override
    public URLRepository save(URLData urlData, Handler<AsyncResult<URLData>> resultHandler) {
        return null;
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
                    List<URLData> urlDataList = ar.result()
                            .stream()
                            .map(data -> new URLData(
                                    data.getString("urlId"),
                                    data.getString("url"),
                                    data.getString("user"),
                                    Json.decodeValue(data.getJsonObject("info").toBuffer(), Info.class),
                                    data.getString("createdOn")
                            ))
                            .collect(Collectors.toList());

                    resultHandler.handle(Future.succeededFuture(urlDataList));
                });

        return this;
    }

    @Override
    public URLRepository findById(String id, Handler<AsyncResult<URLData>> resultHandler) {
        return null;
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

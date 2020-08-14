package io.urlshortener.helper;

import io.urlshortener.model.URLData;
import io.urlshortener.model.URLDataConverter;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.mongo.MongoClient;
import lombok.extern.log4j.Log4j2;

import java.util.List;
import java.util.Optional;

/**
 * Common class to abstract recurring functionalities of CRUD
 */
@Log4j2
public class RepositoryHelper {

    private final MongoClient mongoClient;
    private final String dbCollection;

    /**
     * Initializes the class with mongoClient and the dbCollection name
     *
     * @param mongoClient  - The mongo client to used to perform operations
     * @param dbCollection - The name of collection to look in
     */
    public RepositoryHelper(MongoClient mongoClient, String dbCollection) {
        this.mongoClient = mongoClient;
        this.dbCollection = dbCollection;
    }

    /**
     * Takes a query and returns a collections of results
     *
     * @param query - The query to execute
     * @return Collection of fetched elements
     */
    public Future<List<JsonObject>> find(JsonObject query) {

        Promise<List<JsonObject>> promise = Promise.promise();

        mongoClient.find(dbCollection,
                query,
                ar -> {
                    if (ar.failed()) {
                        LOGGER.error("Error occurred while fetching data");
                        promise.fail(ar.cause());
                        return;
                    }
                    List<JsonObject> result = ar.result();
                    LOGGER.info("result optional : " + result);
                    promise.complete(result);
                });
        return promise.future();
    }

    /**
     * Maps the {@link JsonObject} fetched to {@link URLData}
     *
     * @param json - The fetched JSON
     * @return The mapped {@link URLData}
     */
    public URLData mapToURLData(JsonObject json) {
//        if(json == null) return null;
        URLData urlData = new URLData();
        URLDataConverter.fromJson(json, urlData);
        return urlData;
    }
}

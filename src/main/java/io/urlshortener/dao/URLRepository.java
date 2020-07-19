package io.urlshortener.dao;

import io.urlshortener.model.URLData;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.ext.mongo.MongoClient;

import java.util.List;

/**
 * TODO : More DB interacting methods to be added
 */
@ProxyGen
@VertxGen
public interface URLRepository {

    String SERVICE_ADDRESS = "url-repository-service";

    @Fluent
    URLRepository save(URLData urlData, Handler<AsyncResult<URLData>> resultHandler);

    @Fluent
    URLRepository findAll(Handler<AsyncResult<List<URLData>>> resultHandler);

    @Fluent
    URLRepository findById(String id, Handler<AsyncResult<URLData>> resultHandler);

    @Fluent
    URLRepository findByURL(String url, Handler<AsyncResult<List<URLData>>> resultHandler);

    @Fluent
    URLRepository findByUser(String user, Handler<AsyncResult<List<URLData>>> resultHandler);

    static URLRepository createProxy(Vertx vertx, String address) {
        return new URLRepositoryVertxEBProxy(vertx, address);
    }

    static URLRepository create(MongoClient client) {
        return new URLRepositoryImpl(client);
    }
}

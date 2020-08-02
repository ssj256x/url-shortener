package io.urlshortener.dao;

import io.urlshortener.dao.impl.URLRepositoryImpl;
import io.urlshortener.model.URLData;
import io.vertx.codegen.annotations.Fluent;
import io.vertx.codegen.annotations.ProxyGen;
import io.vertx.codegen.annotations.VertxGen;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;

import java.util.List;

/**
 * URL Repository service interface
 * TODO : More DB interacting methods to be added
 */
@ProxyGen
@VertxGen
public interface URLRepository {

    /**
     * Service address placeholder
     */
    String SERVICE_ADDRESS = "url-repository-service";

    /**
     * Save a single {@link URLData}
     *
     * @param urlData       - The data to be inserted
     * @param resultHandler - Result handler as part of ProxyGen
     * @return Same class instance
     */
    @Fluent
    URLRepository save(URLData urlData, Handler<AsyncResult<String>> resultHandler);

    /**
     * Fetches all URLs
     *
     * @param resultHandler - Result handler as part of ProxyGen
     * @return Same class instance
     */
    @Fluent
    URLRepository findAll(Handler<AsyncResult<List<URLData>>> resultHandler);

    /**
     * Find a single {@link URLData} by ID
     *
     * @param id            - ID of the URL to be fetched
     * @param resultHandler - Result handler as part of ProxyGen
     * @return Same class instance
     */
    @Fluent
    URLRepository findById(String id, Handler<AsyncResult<URLData>> resultHandler);

    /**
     * Find a single {@link URLData} by URL
     *
     * @param url           - The URL to be fetched
     * @param resultHandler - Result handler as part of ProxyGen
     * @return Same class instance
     */
    @Fluent
    URLRepository findByURL(String url, Handler<AsyncResult<List<URLData>>> resultHandler);

    /**
     * Find the URLs for the passed User
     *
     * @param user          - The user whose URLs are to be fetched
     * @param resultHandler - Result handler as part of ProxyGen
     * @return Same class instance
     */
    @Fluent
    URLRepository findByUser(String user, Handler<AsyncResult<List<URLData>>> resultHandler);

    static URLRepository createProxy(Vertx vertx, String address) {
        return new URLRepositoryVertxEBProxy(vertx, address);
    }

    static URLRepository create(Vertx vertx, JsonObject config) {
        return new URLRepositoryImpl(vertx, config);
    }
}

package io.urlshortener.service;

import io.urlshortener.model.URLData;
import io.urlshortener.service.impl.URLServiceImpl;
import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;
import io.vertx.ext.web.api.generator.WebApiServiceGen;

/**
 * The service class that interfaces with the openapi.json functions. The proxy for this class is auto generated
 */
@WebApiServiceGen
public interface URLService {

    static URLService create(Vertx vertx, JsonObject config) {
        return new URLServiceImpl(vertx, config);
    }

    /**
     * To create a URL.
     *
     * @param body       - The URL and data to be shortened.
     * @param context       - Operation Context as part of WebServiceGen
     * @param resultHandler - ResultHandler as part of WebServiceGen
     */
    void createURL(URLData body,
                   OperationRequest context,
                   Handler<AsyncResult<OperationResponse>> resultHandler);

    /**
     * To get all URLs.
     *
     * @param context       - Operation Context as part of WebServiceGen
     * @param resultHandler - ResultHandler as part of WebServiceGen
     */
    void getAllURLs(OperationRequest context,
                    Handler<AsyncResult<OperationResponse>> resultHandler);

    /**
     * This function fetches a given URL using the URL ID and redirects to the fetched URL
     *
     * @param urlId         - URL ID to be fetched
     * @param context       - Operation Context as part of WebServiceGen
     * @param resultHandler - ResultHandler as part of WebServiceGen
     */
    void redirectToURL(String id,
                       OperationRequest context,
                       Handler<AsyncResult<OperationResponse>> resultHandler);

    /**
     * Fetches a URL based on the URL ID
     *
     * @param id         - URL ID to be fetched
     * @param context       - Operation Context as part of WebServiceGen
     * @param resultHandler - ResultHandler as part of WebServiceGen
     */
    void getUrlById(String id,
                    OperationRequest context,
                    Handler<AsyncResult<OperationResponse>> resultHandler);

    /**
     * Deletes a {@link URLData} for the given URL ID
     *
     * @param urlId         - URL ID to be fetched
     * @param context       - Operation Context as part of WebServiceGen
     * @param resultHandler - ResultHandler as part of WebServiceGen
     */
    void deleteURLData(String id,
                       OperationRequest context,
                       Handler<AsyncResult<OperationResponse>> resultHandler);

    /**
     * Updates a given {@link URLData} for the given URL ID
     *
     * @param urlId         - URL ID to be fetched
     * @param context       - Operation Context as part of WebServiceGen
     * @param resultHandler - ResultHandler as part of WebServiceGen
     */
    void updateURLData(URLData body,
                       OperationRequest context,
                       Handler<AsyncResult<OperationResponse>> resultHandler);
}

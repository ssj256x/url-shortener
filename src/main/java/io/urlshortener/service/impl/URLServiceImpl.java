package io.urlshortener.service.impl;

import io.urlshortener.dao.URLRepository;
import io.urlshortener.model.URLData;
import io.urlshortener.service.URLService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class URLServiceImpl implements URLService {

    private final URLRepository urlRepository;

    public URLServiceImpl(Vertx vertx, JsonObject config) {
        urlRepository = URLRepository.create(vertx, config);
    }

    @Override
    public void createURL(URLData urlData, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {

    }

    @Override
    public void getAllURLs(OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        LOGGER.info("Inside getAllURLs");
        try {
            resultHandler.handle(
                    Future.succeededFuture(
                            OperationResponse.completedWithJson(
                                    JsonObject.mapFrom(urlRepository.findAll(AsyncResult::succeeded))
                            )
                    )
            );
        } catch (Exception e) {
            LOGGER.error(e.getStackTrace(), e);
        }
    }

    @Override
    public void redirectToURL(String urlId, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {
        LOGGER.info("Inside redirectToURL");
        resultHandler.handle(
                Future.succeededFuture(
                        OperationResponse.completedWithJson(
                                JsonObject.mapFrom("Successful GET redirect! " + urlId)
                        )
                )
        );
    }

    @Override
    public void getUrlById(String urlId, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {

    }

    @Override
    public void deleteURLData(String urlId, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {

    }

    @Override
    public void updateURLData(String urlId, OperationRequest context, Handler<AsyncResult<OperationResponse>> resultHandler) {

    }
}

package io.urlshortener.service.impl;

import io.urlshortener.dao.URLRepository;
import io.urlshortener.model.URLData;
import io.urlshortener.model.URLDataConverter;
import io.urlshortener.service.URLService;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;
import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;

@Log4j2
public class URLServiceImpl implements URLService {

    private final URLRepository urlRepository;

    public URLServiceImpl(Vertx vertx, JsonObject config) {
        urlRepository = URLRepository.create(vertx, config);
    }

    @Override
    public void createURL(URLData body,
                          OperationRequest context,
                          Handler<AsyncResult<OperationResponse>> resultHandler) {

        LOGGER.info("Inside createURL");
        try {
            // TODO : Generate the URL ID using a generator
            body.setCreatedOn(new Timestamp(System.currentTimeMillis()).toString());
            LOGGER.info("Saving URLData : {}", body);
            // TODO : Generate the body and send it as response
            resultHandler.handle(
                    Future.succeededFuture(
                            OperationResponse.completedWithJson(
                                    JsonObject.mapFrom(urlRepository.save(body, AsyncResult::succeeded)))));
        } catch (Exception e) {
            LOGGER.error(e.getStackTrace(), e);
        }
    }

    @Override
    public void getAllURLs(OperationRequest context,
                           Handler<AsyncResult<OperationResponse>> resultHandler) {
        LOGGER.info("Inside getAllURLs");
        try {
            urlRepository.findAll(ar -> {
                if (ar.failed()) {
                    resultHandler.handle(Future.failedFuture(ar.cause()));
                    LOGGER.error("Error while fetching data");
                }
                resultHandler.handle(
                        Future.succeededFuture(
                                OperationResponse.completedWithJson(Buffer.buffer(ar.result().toString()))));
            });
        } catch (Exception e) {
            LOGGER.error(e.getStackTrace(), e);
        }
    }

    @Override
    public void redirectToURL(String id,
                              OperationRequest context,
                              Handler<AsyncResult<OperationResponse>> resultHandler) {
        LOGGER.info("Inside redirectToURL");

        try {
            urlRepository.findById(id, ar -> {
                if (ar.failed()) {
                    resultHandler.handle(Future.failedFuture(ar.cause()));
                    LOGGER.error("Error while fetching data");
                    return;
                }
                resultHandler.handle(
                        Future.succeededFuture(
                                new OperationResponse()
                                        .setStatusCode(302)
                                        .putHeader("Location", ar.result().getUrl())
                        ));
            });
        } catch (Exception e) {
            LOGGER.error(e.getStackTrace(), e);
        }
    }

    @Override
    public void getUrlById(String id,
                           OperationRequest context,
                           Handler<AsyncResult<OperationResponse>> resultHandler) {

        LOGGER.info("Inside getUrlById for ID : {}", id);

        try {

            urlRepository.findById(id, ar -> {
                if (ar.succeeded()) {
                    resultHandler.handle(
                            Future.succeededFuture(
                                    OperationResponse.completedWithJson(ar.result().toJson())));
                } else {
                    resultHandler.handle(Future.failedFuture(ar.cause()));
                    LOGGER.error("Error while fetching data");
                }
            });


        } catch (Exception e) {
            LOGGER.error(e.getStackTrace(), e);
        }
    }

    @Override
    public void deleteURLData(String urlId,
                              OperationRequest context,
                              Handler<AsyncResult<OperationResponse>> resultHandler) {

    }

    @Override
    public void updateURLData(String urlId,
                              OperationRequest context,
                              Handler<AsyncResult<OperationResponse>> resultHandler) {

    }
}

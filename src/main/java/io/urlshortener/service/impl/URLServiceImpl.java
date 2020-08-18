package io.urlshortener.service.impl;

import io.urlshortener.dao.URLRepository;
import io.urlshortener.model.URLData;
import io.urlshortener.service.URLService;
import io.urlshortener.util.RandomStringGenerator;
import io.urlshortener.util.ResponseHelper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;
import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;
import java.util.stream.Collectors;

@Log4j2
public class URLServiceImpl implements URLService {

    private final URLRepository urlRepository;
    private final ResponseHelper responseHelper;
    private final RandomStringGenerator randomStringGenerator;
    private static final Integer RAND_STR_LEN = 7;

    public URLServiceImpl(Vertx vertx, JsonObject config) {
        urlRepository = URLRepository.create(vertx, config);
        responseHelper = new ResponseHelper();
        randomStringGenerator = new RandomStringGenerator();
    }

    @Override
    public void createURL(URLData body,
                          OperationRequest context,
                          Handler<AsyncResult<OperationResponse>> resultHandler) {

        LOGGER.info("Inside createURL");

        body.setUrlId(randomStringGenerator.generateAlphaNumericString(RAND_STR_LEN));
        body.setCreatedOn(new Timestamp(System.currentTimeMillis()).toString());

        LOGGER.info("Saving URLData : {}", body);

        urlRepository.save(body, ar -> {
            if (ar.failed()) {
                resultHandler.handle(Future.failedFuture(ar.cause()));
                LOGGER.error("Error while fetching data");
                return;
            }
            LOGGER.info("Response after saving : {}", ar.result());
            resultHandler.handle(Future.succeededFuture(responseHelper.created(body)));
        });
    }

    @Override
    public void getAllURLs(OperationRequest context,
                           Handler<AsyncResult<OperationResponse>> resultHandler) {
        LOGGER.info("Inside getAllURLs");
        urlRepository.findAll(ar -> {
            if (ar.failed()) {
                resultHandler.handle(Future.failedFuture(ar.cause()));
                LOGGER.error("Error while fetching data");
                return;
            }
            resultHandler.handle(
                    Future.succeededFuture(
                            responseHelper.ok(ar
                                    .result()
                                    .stream()
                                    .map(URLData::toJson).collect(Collectors.toList()))));
        });
    }

    @Override
    public void redirectToURL(String id,
                              OperationRequest context,
                              Handler<AsyncResult<OperationResponse>> resultHandler) {
        LOGGER.info("Inside redirectToURL with id : {}", id);

        if(responseHelper.urlIdEmptyResponse(id, resultHandler)) return;

        urlRepository.findById(id, ar -> {
            if (ar.failed()) {
                resultHandler.handle(Future.failedFuture(ar.cause()));
                LOGGER.error("Error while fetching data");
                return;
            }
            resultHandler.handle(Future.succeededFuture(responseHelper.redirectToUrlResponse(ar.result())));
        });
    }

    @Override
    public void getUrlById(String id,
                           OperationRequest context,
                           Handler<AsyncResult<OperationResponse>> resultHandler) {

        LOGGER.info("Inside getUrlById for ID : {}", id);

        if(responseHelper.urlIdEmptyResponse(id, resultHandler)) return;

        urlRepository.findById(id, ar -> {
            if (ar.failed()) {
                resultHandler.handle(Future.failedFuture(ar.cause()));
                LOGGER.error("Error while fetching data");
                return;
            }
            resultHandler.handle(Future.succeededFuture(responseHelper.fetchUrlResponse(ar.result())));
        });

    }

    @Override
    public void deleteURLData(String id,
                              OperationRequest context,
                              Handler<AsyncResult<OperationResponse>> resultHandler) {

        LOGGER.info("Inside deleteURLData for id : {}", id);

        if(responseHelper.urlIdEmptyResponse(id, resultHandler)) return;

        urlRepository.delete(id, ar -> {

            if (ar.failed()) {
                resultHandler.handle(Future.failedFuture(ar.cause()));
                LOGGER.error("Error while fetching data");
                return;
            }

            resultHandler.handle(Future.succeededFuture(responseHelper.ok(ar.result())));
        });
    }

    @Override
    public void updateURLData(URLData body,
                              OperationRequest context,
                              Handler<AsyncResult<OperationResponse>> resultHandler) {

        LOGGER.info("Inside updateURLData for body : {}", body);

        urlRepository.update(body, ar -> {
            if (ar.failed()) {
                resultHandler.handle(Future.failedFuture(ar.cause()));
                LOGGER.error("Error while fetching data");
                return;
            }

            LOGGER.info("Response after updating : {}", ar.result());
            resultHandler.handle(Future.succeededFuture(responseHelper.created(ar.result())));
        });
    }
}

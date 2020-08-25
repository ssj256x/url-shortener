package io.urlshortener.service.impl;

import io.urlshortener.dao.URLRepository;
import io.urlshortener.model.URLData;
import io.urlshortener.service.URLService;
import io.urlshortener.helper.RandomStringGenerator;
import io.urlshortener.helper.ResponseHelper;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationRequest;
import io.vertx.ext.web.api.OperationResponse;
import lombok.extern.log4j.Log4j2;

import java.sql.Timestamp;

@Log4j2
public class URLServiceImpl implements URLService {

    private final URLRepository urlRepository;
    private final ResponseHelper responseHelper;
    private final RandomStringGenerator randomStringGenerator;
    private static final Integer RAND_STR_LEN = 7;

    public URLServiceImpl(Vertx vertx, JsonObject config) {
        urlRepository = URLRepository.createProxy(vertx, URLRepository.SERVICE_ADDRESS);
        responseHelper = new ResponseHelper();
        randomStringGenerator = new RandomStringGenerator();
    }

    @Override
    public void createURL(URLData body,
                          OperationRequest context,
                          Handler<AsyncResult<OperationResponse>> resultHandler) {

        LOGGER.info("Inside createURL");

        //TODO : Validate incoming request for URL Body
        //TODO : Use a Key Generation Service to generate keys

        body.setUrlId(randomStringGenerator.generateAlphaNumericString(RAND_STR_LEN));
        body.setCreatedOn(new Timestamp(System.currentTimeMillis()).toString());

        LOGGER.info("Saving URLData : {}", body);

        urlRepository.save(body, ar -> {
            if (ar.failed()) {
                responseHelper.handleInternalServerError(ar.cause(),
                        "Error occurred while creating URL data",
                        resultHandler);
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
                responseHelper.handleInternalServerError(ar.cause(),
                        "Error occurred while fetching URLs",
                        resultHandler);
                return;
            }
            responseHelper.handleListResponse(ar.result(), resultHandler);
        });
    }

    @Override
    public void redirectToURL(String id,
                              OperationRequest context,
                              Handler<AsyncResult<OperationResponse>> resultHandler) {

        LOGGER.info("Inside redirectToURL with id : {}", id);

        if (responseHelper.handleIfUrlEmpty(id, resultHandler)) return;

        urlRepository.findById(id, ar -> {
            if (ar.failed()) {
                responseHelper.handleInternalServerError(ar.cause(),
                        "Error occurred while creating URL",
                        resultHandler);
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

        if (responseHelper.handleIfUrlEmpty(id, resultHandler)) return;

        urlRepository.findById(id, ar -> {
            if (ar.failed()) {
                responseHelper.handleInternalServerError(ar.cause(),
                        "Error while fetching URL data",
                        resultHandler);
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

        if (responseHelper.handleIfUrlEmpty(id, resultHandler)) return;

        urlRepository.delete(id, ar -> {

            if (ar.failed()) {
                responseHelper.handleInternalServerError(ar.cause(),
                        "Error while deleting URL data",
                        resultHandler);
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

        //TODO : map from body param to new URLData obj and perform update

        urlRepository.update(body, ar -> {
            if (ar.failed()) {
                responseHelper.handleInternalServerError(ar.cause(),
                        "Error while updating URL data",
                        resultHandler);
                return;
            }

            LOGGER.info("Response after updating : {}", ar.result());
            resultHandler.handle(Future.succeededFuture(responseHelper.created(ar.result())));
        });
    }
}

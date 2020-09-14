package io.urlshortener.helper;

import io.urlshortener.model.ErrorResponse;
import io.urlshortener.model.URLData;
import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Handler;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.api.OperationResponse;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static javax.ws.rs.core.MediaType.*;
import static javax.ws.rs.core.Response.Status.*;
import static javax.ws.rs.core.HttpHeaders.*;

/**
 * This class helps generate the {@link OperationResponse} object for multiple response types
 */
@Log4j2
public class ResponseHelper {

    /**
     * Generates the {@link OperationResponse} for fetching a URL. It returns both Successful and Error
     * responses.
     *
     * @param urlData - The response object
     * @return Generated response
     */
    public OperationResponse fetchUrlResponse(URLData urlData) {
        return urlData != null ?
                ok(urlData.toJson()) :
                notFound("URLNTFND", "URL Not Found");
    }

    /**
     * Generates the {@link OperationResponse} for redirecting to the fetched URL. It returns both Successful
     * and Error responses.
     *
     * @param urlData - The response object
     * @return Generated response
     */
    public OperationResponse redirectToUrlResponse(URLData urlData) {
        return urlData != null ?
                redirect(urlData.getUrl()) :
                notFound("URLNTFND", "URL Not Found");
    }

    /**
     * Generates the {@link OperationResponse} fetched lists of {@link URLData}.
     *
     * @param urlDataList - The response object
     */
    public void handleListResponse(List<URLData> urlDataList,
                                   Handler<AsyncResult<OperationResponse>> resultHandler) {

        List<JsonObject> jsonObjectList = new ArrayList<>();

        if (urlDataList != null) {
            jsonObjectList = urlDataList
                    .stream()
                    .map(URLData::toJson)
                    .collect(Collectors.toList());
        }

        resultHandler.handle(
                Future.succeededFuture(
                        ok(jsonObjectList)));
    }

    /**
     * Generates the failed future if passed URL ID doesn't exist
     *
     * @param urlId         - URL ID to check
     * @param resultHandler - The result handler
     * @return true or false based on result
     */
    public boolean handleIfUrlEmpty(String urlId,
                                    Handler<AsyncResult<OperationResponse>> resultHandler) {

        if (urlId == null || StringUtils.isBlank(urlId)) {
            resultHandler.handle(
                    Future.succeededFuture(
                            badRequest("URLIDEMPTY", "URL ID cannot be Empty")));
            return true;
        }
        return false;
    }

    /**
     * Generates a 500 {@link OperationResponse} object
     *
     * @param t             - The error object
     * @param msg           - The message to be used
     * @param resultHandler - The result handler
     */
    public void handleInternalServerError(Throwable t,
                                          String msg,
                                          Handler<AsyncResult<OperationResponse>> resultHandler) {

        LOGGER.error(msg);
        LOGGER.error(t.getStackTrace(), t);
        resultHandler.handle(Future.succeededFuture(internalError(null, msg)));
    }


    /**
     * Generates a 200 {@link OperationResponse} object
     *
     * @param response - The JSON object to be enriched in the response
     * @return The OperationResponse object
     */
    public OperationResponse ok(Object response) {
        return new OperationResponse()
                .setStatusCode(OK.getStatusCode())
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .setPayload(Buffer.buffer(response.toString()));
    }

    /**
     * Generates a 201 {@link OperationResponse} object
     *
     * @param response - The JSON object to be enriched in the response
     * @return The OperationResponse object
     */
    public OperationResponse created(Object response) {
        return new OperationResponse()
                .setStatusCode(OK.getStatusCode())
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .setPayload(Buffer.buffer(response.toString()));
    }

    /**
     * Generates a 404 {@link OperationResponse} object
     *
     * @param errorCode - The error code to be used
     * @param errorText - The error text to be used
     * @return The OperationResponse object
     */
    public OperationResponse notFound(String errorCode, String errorText) {
        return generateResponse(NOT_FOUND.getStatusCode(),
                nullCheck(errorCode, "DATANTFND"),
                nullCheck(errorText, "Data Not Found"));
    }

    /**
     * Generates a 302 {@link OperationResponse} object
     *
     * @param urlToRedirect - The url to be redirected to
     * @return The OperationResponse object
     */
    public OperationResponse redirect(String urlToRedirect) {

        return new OperationResponse()
                .setStatusCode(FOUND.getStatusCode())
                .putHeader("Location", urlToRedirect);
    }

    /**
     * Generates a 400 {@link OperationResponse} object
     *
     * @param errorCode - The error code to be used
     * @param errorText - The error text to be used
     * @return The OperationResponse object
     */
    public OperationResponse badRequest(String errorCode, String errorText) {
        return generateResponse(BAD_REQUEST.getStatusCode(),
                nullCheck(errorCode, "BADRQST"),
                nullCheck(errorText, "Bad Request"));
    }

    /**
     * Generates a 500 {@link OperationResponse} object
     *
     * @param errorCode - The error code to be used
     * @param errorText - The error text to be used
     * @return The OperationResponse object
     */
    public OperationResponse internalError(String errorCode, String errorText) {
        return generateResponse(INTERNAL_SERVER_ERROR.getStatusCode(),
                nullCheck(errorCode, "INSVRERR"),
                nullCheck(errorText, "Internal Server Error Occurred"));
    }

    /**
     * Generates a 501 {@link OperationResponse} object
     *
     * @param errorCode - The error code to be used
     * @param errorText - The error text to be used
     * @return The OperationResponse object
     */
    public OperationResponse notImplemented(String errorCode, String errorText) {
        return generateResponse(NOT_IMPLEMENTED.getStatusCode(),
                nullCheck(errorCode, "NTIMPLTD"),
                nullCheck(errorText, "Method Not Implemented"));
    }

    /**
     * Generates a 503 {@link OperationResponse} object
     *
     * @param errorCode - The error code to be used
     * @param errorText - The error text to be used
     * @return The OperationResponse object
     */
    public OperationResponse serviceUnavailable(String errorCode, String errorText) {
        return generateResponse(SERVICE_UNAVAILABLE.getStatusCode(),
                nullCheck(errorCode, "SRVCUNAVL"),
                nullCheck(errorText, "Service Unavailable"));
    }

    /**
     * Maps the passed params to the {@link OperationResponse} object
     *
     * @param httpCode  - The http code of the response
     * @param errorCode - The error code for the response
     * @param errorText - The error text for the response
     * @return The OperationResponse object
     */
    private OperationResponse generateResponse(int httpCode, String errorCode, String errorText) {

        JsonObject errorResponse = new ErrorResponse(errorCode, errorText).toJson();
        Buffer response = Buffer.buffer(errorResponse.toString());

        return new OperationResponse()
                .setStatusCode(httpCode)
                .putHeader(CONTENT_TYPE, APPLICATION_JSON)
                .setPayload(response);
    }

    /**
     * Checks if the 'fieldVal' is null and returns 'defaultVal' if true else returns 'fieldVal'
     *
     * @param fieldVal   - The value to be checked for null
     * @param defaultVal - The default value to be returned if 'fieldVal' is null
     * @return Value based on 'fieldVal' check
     */
    private String nullCheck(String fieldVal, String defaultVal) {
        return fieldVal == null ? defaultVal : fieldVal;
    }
}

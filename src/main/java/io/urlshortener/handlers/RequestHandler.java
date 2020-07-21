package io.urlshortener.handlers;

import io.urlshortener.dao.URLRepository;
import io.urlshortener.model.URLCreatedResponse;
import io.urlshortener.model.URLData;
import io.urlshortener.util.ApiHeaders;
import io.urlshortener.util.RandomStringGenerator;
import io.vertx.core.http.RequestOptions;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.client.WebClient;
import lombok.extern.log4j.Log4j2;

import javax.ws.rs.core.MediaType;
import java.sql.Timestamp;

@Log4j2
public class RequestHandler {

    private final URLRepository urlRepository;
    private final RandomStringGenerator stringGenerator;

    public RequestHandler(URLRepository urlRepository) {
        this.urlRepository = urlRepository;
        this.stringGenerator = new RandomStringGenerator();
    }

    public void handleCreate(RoutingContext ctx) {
        LOGGER.info("Handle create called");

        URLData urlData = Json.decodeValue(ctx.getBodyAsString(), URLData.class);

        String generatedId = stringGenerator.generateAlphaNumericString(7);
        String createTs = String.valueOf(new Timestamp(System.currentTimeMillis()));

        urlData.setUrlId(generatedId);
        urlData.setCreatedOn(createTs);

        urlRepository.save(urlData, res -> {
            if (res.failed()) {
                String msg = "Error occurred while inserting data. ";
                LOGGER.error(msg, res.cause());
                ctx.response()
                        .setStatusCode(500)
                        .putHeader(ApiHeaders.CONTENT_TYPE.getValue(), MediaType.TEXT_PLAIN)
                        .end(msg);
                return;
            }
            String host = new RequestOptions().getHost();
            String port = System.getProperty("PORT");

            String url = String.format("http://%s:%s/%s", host, port, generatedId);

            URLCreatedResponse response = new URLCreatedResponse(generatedId, url, createTs);

            ctx.response().setStatusCode(201)
                    .putHeader(ApiHeaders.CONTENT_TYPE.getValue(), MediaType.APPLICATION_JSON)
                    .end(Json.encode(response));
        });
    }

    public void handleGetAll(RoutingContext ctx) {
        LOGGER.info("Handle get all called");

        urlRepository.findAll(res -> {
            if (res.failed()) {
                String msg = "Error occurred fetching all data. ";
                LOGGER.error(msg, res.cause());
                ctx.response()
                        .setStatusCode(500)
                        .putHeader(ApiHeaders.CONTENT_TYPE.getValue(), MediaType.TEXT_PLAIN)
                        .end(msg);
                return;
            }

            ctx.response().setStatusCode(200)
                    .putHeader(ApiHeaders.CONTENT_TYPE.getValue(), MediaType.APPLICATION_JSON)
                    .end(Json.encode(res.result()));
        });
    }

    public void handleGetOne(RoutingContext ctx) {
        LOGGER.info("Handle get one called");

        urlRepository.findById(ctx.request().getParam("id"), res -> {
            if (res.failed()) {
                String msg = "Error occurred fetching all data. ";
                LOGGER.error(msg, res.cause());
                ctx.response()
                        .setStatusCode(500)
                        .putHeader(ApiHeaders.CONTENT_TYPE.getValue(), MediaType.TEXT_PLAIN)
                        .end(msg);
                return;
            }

            ctx.response().setStatusCode(200)
                    .putHeader(ApiHeaders.CONTENT_TYPE.getValue(), MediaType.APPLICATION_JSON)
                    .end(Json.encode(res.result()));
        });
    }

    public void handleRedirect(RoutingContext ctx) {

        urlRepository.findById(ctx.request().getParam("id"), res -> {
            if (res.failed()) {
                String msg = "Error occurred fetching all data. ";
                LOGGER.error(msg, res.cause());
                ctx.response()
                        .setStatusCode(500)
                        .putHeader(ApiHeaders.CONTENT_TYPE.getValue(), MediaType.TEXT_PLAIN)
                        .end(msg);
                return;
            }

            // TODO : Handle errors in and validate data throughout the app
            // TODO : Check for null pointers
            // TODO : Try to figure out unknown behaviour
            String redirectURL = res.result().getUrl();
            LOGGER.info("redirectURL : {}", redirectURL);

            ctx.response()
                    .setStatusCode(302)
                    .putHeader("Location", redirectURL)
                    .end();
        });

    }

    public void handleDelete(RoutingContext ctx) {
        LOGGER.info("Handle delete called");
        ctx.response().setStatusCode(200)
                .putHeader(ApiHeaders.CONTENT_TYPE.getValue(), "text/plain")
                .end("Delete Called");
    }

    public void handleUpdate(RoutingContext ctx) {
        LOGGER.info("Handle update called");
        ctx.response().setStatusCode(200)
                .putHeader(ApiHeaders.CONTENT_TYPE.getValue(), "text/plain")
                .end("Update Called");
    }
}

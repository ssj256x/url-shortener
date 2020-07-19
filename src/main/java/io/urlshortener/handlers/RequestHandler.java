package io.urlshortener.handlers;

import io.urlshortener.dao.URLRepository;
import io.urlshortener.model.URLData;
import io.urlshortener.util.ApiHeaders;
import io.vertx.core.json.Json;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.log4j.Log4j2;

import javax.ws.rs.core.MediaType;

@Log4j2
public class RequestHandler {

    URLRepository urlRepository;

    public RequestHandler(URLRepository urlRepository) {
        this.urlRepository = urlRepository;
    }

    public void handleCreate(RoutingContext ctx) {
        LOGGER.info("Handle create called");

        URLData urlData = Json.decodeValue(ctx.getBodyAsString(), URLData.class);

        urlRepository.save(urlData, res -> {
            if(res.failed()) {
                String msg = "Error occurred while inserting data. ";
                LOGGER.error(msg, res.cause());
                ctx.response()
                        .setStatusCode(500)
                        .putHeader(ApiHeaders.CONTENT_TYPE.getValue(), MediaType.TEXT_PLAIN)
                        .end(msg);
                return;
            }

            ctx.response().setStatusCode(201)
                    .putHeader(ApiHeaders.CONTENT_TYPE.getValue(), MediaType.TEXT_PLAIN)
                    .end("URL Data inserted Successfully");
        });
    }

    public void handleGetAll(RoutingContext ctx) {
        LOGGER.info("Handle get all called");

        urlRepository.findAll(res -> {
            if(res.failed()) {
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
            if(res.failed()) {
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

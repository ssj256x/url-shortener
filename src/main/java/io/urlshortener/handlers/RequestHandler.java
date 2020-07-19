package io.urlshortener.handlers;

import io.urlshortener.util.ApiHeaders;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.log4j.Log4j2;

@Log4j2
public class RequestHandler {

    public void handleCreate(RoutingContext ctx) {
        LOGGER.info("Handle create called");
        ctx.response().setStatusCode(200)
                .putHeader(ApiHeaders.CONTENT_TYPE.getValue(), "text/plain")
                .end("Create Called");
    }

    public void handleGetAll(RoutingContext ctx) {
        LOGGER.info("Handle get all called");
        ctx.response().setStatusCode(200)
                .putHeader(ApiHeaders.CONTENT_TYPE.getValue(), "text/plain")
                .end("Get All Called");
    }

    public void handleGetOne(RoutingContext ctx) {
        LOGGER.info("Handle get one called");
        ctx.response().setStatusCode(200)
                .putHeader(ApiHeaders.CONTENT_TYPE.getValue(), "text/plain")
                .end("Get One Called");
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

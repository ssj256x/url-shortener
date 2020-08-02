package io.urlshortener.verticles;

import io.urlshortener.util.GenericConstants;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.api.contract.openapi3.OpenAPI3RouterFactory;
import lombok.extern.log4j.Log4j2;

/**
 * Verticle to start the Http Server and implement routing from openapi.json file
 */
@Log4j2
public class RestUrlApiVerticle extends RestApiVerticle {

    private int port;
    private String host;

    @Override
    public void start(Promise<Void> promise) {
        startHttpServer().onComplete(promise);
    }

    /**
     * Starts the Http Server and loads the routing and handling properties from openapi.json
     *
     * @return Future object
     */
    private Future<Void> startHttpServer() {

        LOGGER.info("Starting Server");

        Promise<Void> promise = Promise.promise();
        String openApiSpec = config().getString("open-api-spec");
        LOGGER.info("Creating endpoints from Api Spec : {}", openApiSpec);

        OpenAPI3RouterFactory.create(this.vertx, openApiSpec, ar -> {

            if (ar.failed()) {
                LOGGER.error("Error while reading from API-Spec. {}", ar.cause().getMessage());
                promise.fail(ar.cause());
                return;
            }

            try {
                OpenAPI3RouterFactory openAPI3RouterFactory = ar.result();

                // Mount services on Event Bus based on extensions
                openAPI3RouterFactory.mountServicesFromExtensions();

                LOGGER.info("openAPI3RouterFactory : {}", openAPI3RouterFactory);

                // Fetching the Router defined in api contract
                Router router = openAPI3RouterFactory.getRouter();

                port = config().getInteger(GenericConstants.HTTP_PORT.getValue(), 8080);
                host = config().getString("order.http.address", "127.0.0.1");

                createHttpServer(router, host, port);

            } catch (Exception e) {
                LOGGER.error(e.getStackTrace(), e);
                promise.fail(e.getCause());
            }
        });

        LOGGER.info("Server started at port : {}", port);

        return promise.future();
    }
}

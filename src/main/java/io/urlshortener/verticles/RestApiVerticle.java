package io.urlshortener.verticles;

import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;

/**
 * Common REST Api verticle for performing commons tasks
 */
public class RestApiVerticle extends BaseMicroserviceVerticle {

    /**
     * Creates a HttpServer based on the passed parameters.
     *
     * @param router - {@link Router} object for different endpoints.
     * @param host   - Host name of deployment.
     * @param port   - The port on which the server starts.
     * @return Future object
     */
    public Future<Void> createHttpServer(Router router, String host, int port) {
        Promise<HttpServer> promise = Promise.promise();
        this.vertx
                .createHttpServer()
                .requestHandler(router)
                .listen(port, host, listen -> {
                    if (listen.succeeded()) promise.complete();
                    else promise.fail(listen.cause());
                });
        return promise.future().map(r -> null);
    }
}

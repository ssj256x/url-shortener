package io.urlshortener.verticles;

import io.urlshortener.dao.URLRepository;
import io.urlshortener.handlers.FailureHandler;
import io.urlshortener.handlers.RequestHandler;
import io.urlshortener.util.ApiHeaders;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import lombok.extern.log4j.Log4j2;

import javax.ws.rs.core.MediaType;

@Log4j2
public class Server extends AbstractVerticle {

    int port = 8090;

    @Override
    public void start(Promise<Void> promise) {
        System.setProperty("PORT", String.valueOf(port));
        try {

            vertx.createHttpServer()
                    .requestHandler(getRouter())
                    .listen(port);

            promise.complete();

        } catch (Exception e) {
            promise.fail(e);
        }
    }

    private Router getRouter() {
        RequestHandler requestHandler = new RequestHandler(URLRepository.createProxy(vertx,
                URLRepository.SERVICE_ADDRESS));

        Router router = Router.router(vertx);
        router.route()
                .handler(BodyHandler.create())
                .failureHandler(new FailureHandler());

        router.get("/")
                .handler(ctx -> ctx.response()
                        .setStatusCode(200)
                        .putHeader(ApiHeaders.CONTENT_TYPE.getValue(), MediaType.TEXT_PLAIN)
                        .end(String.format("Application Started on Port %d", port)));

        router.get("/:id")
                .handler(requestHandler::handleRedirect);

        router.post("/create")
                .handler(requestHandler::handleCreate);
        router.get("/all")
                .handler(requestHandler::handleGetAll);
        router.get("/get/:id")
                .handler(requestHandler::handleGetOne);
        router.get("/delete")
                .handler(requestHandler::handleDelete);
        router.get("/update")
                .handler(requestHandler::handleUpdate);

        return router;
    }
}

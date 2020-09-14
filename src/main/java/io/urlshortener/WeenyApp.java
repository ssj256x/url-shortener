package io.urlshortener;

import io.urlshortener.service.URLService;
import io.urlshortener.util.Address;
import io.urlshortener.util.Configuration;
import io.urlshortener.verticles.BaseMicroserviceVerticle;
import io.urlshortener.verticles.RestUrlApiVerticle;
import io.urlshortener.verticles.URLRepositoryVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.serviceproxy.ServiceBinder;
import lombok.extern.log4j.Log4j2;

/**
 * Main application starting verticle
 */
@Log4j2
public class WeenyApp extends BaseMicroserviceVerticle {

    private JsonObject config;

    public static void main(String[] args) throws Exception {
        WeenyApp weenyApp = new WeenyApp(Vertx.vertx());
        weenyApp.start();
    }

    public WeenyApp(Vertx vertx) {
        this.vertx = vertx;
    }

    @Override
    public void start() throws Exception {
        super.start();

        new Configuration(vertx)
                .initConfig()
                .compose(c -> {
                    config = c;
                    return Future.succeededFuture(c);
                })
                .compose(c -> {
                    deployURLRepositoryService();
                    return Future.succeededFuture(c);
                })
                .compose(c -> {
                    registerURLService();
                    return Future.succeededFuture(c);
                })
                .compose(c -> deployRestService());
    }

    /**
     * Deploys the {@link RestUrlApiVerticle}
     *
     * @return Future wrapped object
     */
    private Future<Void> deployRestService() {
        LOGGER.info("Inside deployRestService");
        Promise<Void> promise = Promise.promise();
        vertx.deployVerticle(new RestUrlApiVerticle(), new DeploymentOptions().setConfig(config));
        return promise.future();
    }

    /**
     * Deploys the {@link URLRepositoryVerticle}
     *
     * @return Future wrapped object
     */
    private Future<Void> deployURLRepositoryService() {
        LOGGER.info("Inside deployURLRepositoryService");
        Promise<Void> promise = Promise.promise();
        vertx.deployVerticle(new URLRepositoryVerticle(), new DeploymentOptions().setConfig(config));
        return promise.future();
    }

    /**
     * Registers the {@link URLService} for handling HTTP requests
     *
     * @return Future wrapped object
     */
    private Future<Void> registerURLService() {
        Promise<Void> promise = Promise.promise();
        new ServiceBinder(vertx)
                .setAddress(Address.OPEN_API.value())
                .register(URLService.class, URLService.create(vertx));
        return promise.future();
    }
}

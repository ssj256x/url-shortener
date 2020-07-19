package io.urlshortener;

import io.urlshortener.util.Configuration;
import io.urlshortener.verticles.Database;
import io.urlshortener.verticles.Server;
import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.core.*;
import io.vertx.core.json.JsonObject;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;

/**
 * This class is the starting point of the application.
 */
@Log4j2
public class UrlShortenerApplication {

    private final Configuration configuration;
    private Vertx vertx;
    private VertxOptions vertxOptions;
    private JsonObject config;

    UrlShortenerApplication() {
        configuration = new Configuration();
    }

    public static void main(String[] args) {
        LOGGER.info("Starting Application...");

        UrlShortenerApplication application = new UrlShortenerApplication();

        application.start()
                .onSuccess(handler -> LOGGER.info("Application started on : {}", LocalDateTime.now()))
                .onFailure(handler -> {
                    LOGGER.error("Application Startup Failed!");
                    LOGGER.error(handler.getStackTrace());
                });
    }

    /**
     * Starts the application, initializes configurations and deploys verticles
     *
     * @return Future object
     */
    public Future<Void> start() {
        Promise<Void> promise = Promise.promise();

        configuration.initConfig();

        vertxOptions = new VertxOptions()
                .setEventLoopPoolSize(1)
                .setBlockedThreadCheckInterval(9)
                .setBlockedThreadCheckIntervalUnit(TimeUnit.SECONDS);

        vertx = Vertx.vertx(vertxOptions);

        ConfigRetriever retriever = ConfigRetriever.create(vertx,
                new ConfigRetrieverOptions()
                        .setScanPeriod(60000)
                        .setIncludeDefaultStores(true));

        retriever.getConfig(asyncResult -> {

            if (asyncResult.failed()) {
                LOGGER.error("Could not read from config. ", asyncResult.cause());
                promise.fail(asyncResult.cause());
                return;
            }

            config = asyncResult.result();
            LOGGER.info("Configurations read successfully");

            deployVerticle(Server.class, false);
            deployVerticle(Database.class, true);

            promise.complete();
        });

        return promise.future();
    }

    /**
     * Deploys a given Verticle
     *
     * @param verticle - The verticle class to be deployed
     * @param isWorker - If the given verticle is a worker or not
     */
    private void deployVerticle(Class<? extends Verticle> verticle, boolean isWorker) {

        LOGGER.debug("Deploying Verticle : {}", verticle.getSimpleName());

        DeploymentOptions deploymentOptions = new DeploymentOptions().setConfig(config);

        if (isWorker) {
            deploymentOptions.setWorkerPoolName(config.getString("worker-pool-name"))
                    .setWorkerPoolSize(config.getInteger("worker-pool-size"))
                    .setInstances(vertxOptions.getEventLoopPoolSize());
        }

        vertx.deployVerticle(verticle, deploymentOptions, asyncResult -> {
            if (asyncResult.failed()) {
                LOGGER.error("Verticle deployment failed. ", asyncResult.cause());
                return;
            }
            LOGGER.debug("Deployed Verticle : {}", verticle.getSimpleName());
        });
    }
}

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
 * The Main Verticle for the Application
 */
@Log4j2
public class MainVerticle extends AbstractVerticle {

    private VertxOptions vertxOptions;
    private JsonObject config;

    @Override
    public void start() {

        LOGGER.info("Starting Application...");

        Promise<Void> promise = Promise.promise();
        Configuration configuration = new Configuration();

        configuration.initConfig();

        vertxOptions = new VertxOptions()
                .setEventLoopPoolSize(1)
                .setBlockedThreadCheckInterval(9)
                .setBlockedThreadCheckIntervalUnit(TimeUnit.SECONDS);

        vertx = Vertx.vertx(vertxOptions);

        //TODO: move the below config retreival logic in configuration.java
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

//            deployVerticle(Database.class, false);
            deployVerticle(Server.class, false);

            promise.complete();
        });

        LOGGER.info("Application started on : {}", LocalDateTime.now());
    }

    @Override
    public void stop() throws Exception {
        vertx.close();
        LOGGER.info("Application Closed");
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

package io.urlshortener.util;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

/**
 * This class has functions for all the different configurations that are to be used throughout the application
 */
@Log4j2
public class Configuration {

    private final Vertx vertx;
    private JsonObject config;

    public Configuration(Vertx vertx) {
        this.vertx = vertx;
    }

    /**
     * Loads basic configurations for the Application
     */
    public Future<JsonObject> initConfig() {

        LOGGER.debug("Loading Configuration");

        Promise<Void> promise = Promise.promise();

        String activeProfile = System.getProperty(GenericConstants.ACTIVE_PROFILE.getValue());

        if (StringUtils.isBlank(activeProfile)) {
            activeProfile = Environment.E0.getEnv();
        }

        String configFile = String.format("application-%s.json", activeProfile);

        System.setProperty(GenericConstants.LOGGER_DELEGATE_FACTORY.getValue(), GenericConstants.LOG4J2_LOG_FACTORY.getValue());
        System.setProperty(GenericConstants.VERTX_CONFIG_PATH.getValue(), configFile);
        System.setProperty(GenericConstants.ACTIVE_PROFILE.getValue(), activeProfile);

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

            promise.complete();
        });

        return promise.future().map(c -> config);
    }
}

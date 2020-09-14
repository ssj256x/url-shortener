package io.urlshortener.verticles;

import io.vertx.core.AbstractVerticle;

/**
 * This is a common verticle that provides support for various common microservice activity like
 * service discovery, circuit breaker, log publisher etc..
 *
 * TODO : Add methods for cirucuite breaker, discovery and log publisher
 */
public class BaseMicroserviceVerticle extends AbstractVerticle {

    @Override
    public void start() throws Exception {
    }

    @Override
    public void stop() {

    }
}

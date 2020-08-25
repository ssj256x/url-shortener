package io.urlshortener.repository;

import io.urlshortener.model.URLData;
import io.urlshortener.util.GenericConstants;
import io.urlshortener.verticles.RestUrlApiVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.client.WebClient;
import io.vertx.ext.web.codec.BodyCodec;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import lombok.extern.log4j.Log4j2;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@Log4j2
@ExtendWith(VertxExtension.class)
class SomeTest {

    // Deploy the verticle and execute the test methods when the verticle is successfully deployed
    @BeforeEach
    void deploy_verticle(Vertx vertx, VertxTestContext testContext) {
        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject()
                        .put(GenericConstants.HTTP_PORT.getValue(), 8080)
                        .put(GenericConstants.HTTP_ADDRESS.getValue(), "127.0.0.1")
                        .put("open-api-spec", "/openapi.yaml"));

        vertx.deployVerticle(new RestUrlApiVerticle(),
                options,
                testContext.succeeding(id -> testContext.completeNow()));
    }

    // Repeat this test 3 times
    @Test
    void http_server_check_response(Vertx vertx, VertxTestContext testContext) {

        WebClient client = WebClient.create(vertx);

        LOGGER.info("inside http_server_check_response");

        client.get(8080, "localhost", "/get/rtozUvA")
                .as(BodyCodec.string())
                .send(testContext.succeeding(response -> testContext.verify(() -> {
                    URLData urlData = Json.decodeValue((Buffer) response, URLData.class);
                    Assert.assertEquals(urlData.getUrlId(), "rtozUvA");
                    testContext.completeNow();
                })));
    }
}

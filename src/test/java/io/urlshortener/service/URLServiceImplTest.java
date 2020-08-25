package io.urlshortener.service;

import io.urlshortener.dao.URLRepository;
import io.urlshortener.model.URLData;
import io.urlshortener.util.GenericConstants;
import io.urlshortener.verticles.RestUrlApiVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import io.vertx.ext.web.client.WebClient;
import lombok.extern.log4j.Log4j2;
import org.junit.*;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.modules.junit4.PowerMockRunnerDelegate;

import java.io.IOException;

@Log4j2
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(VertxUnitRunner.class)
@PrepareForTest(URLRepository.class)
public class URLServiceImplTest {

    private Vertx vertx;
    private Integer port;
    private URLRepository urlRepository;

    //TODO : Mock repository class and then run the tests
    @Before
    public void setUp(TestContext context) throws IOException {
        vertx = Vertx.vertx();




        DeploymentOptions options = new DeploymentOptions()
                .setConfig(new JsonObject()
                        .put(GenericConstants.HTTP_PORT.getValue(), 8080)
                        .put(GenericConstants.HTTP_ADDRESS.getValue(), "127.0.0.1")
                        .put("open-api-spec", "/openapi.yaml"));

        vertx.deployVerticle(RestUrlApiVerticle.class.getName(), options);
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    public void getUrlByIdTest(TestContext context) {
        WebClient client = WebClient.create(vertx);

        LOGGER.info("getUrlByIdTest port : {}", port);

        Async async = context.async();

        client.get(8080, "127.0.0.1", "/all")
                .send(response -> {
                    context.verify(ar -> {
                        LOGGER.info("response.result().bodyAsString() : {}", response.result().bodyAsString());
                        URLData urlData = Json.decodeValue(response.result().bodyAsBuffer(), URLData.class);
                        LOGGER.info("urlData : {}", urlData);
                        context.assertEquals(urlData.getUrlId(), "rtozUvA");
                        async.complete();
                    });
                });


        async.awaitSuccess();
    }
}

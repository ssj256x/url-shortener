package io.urlshortener.repository;

import io.urlshortener.dao.URLRepository;
import io.urlshortener.model.URLData;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import lombok.extern.log4j.Log4j2;
import org.junit.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;

import java.util.List;

@Log4j2
@RunWith(VertxUnitRunner.class)
public class UrlRepositoryImplTest {

    private Vertx vertx;
    private URLRepository urlRepository;

    @Before
    public void setUp(TestContext context) {
        vertx = Vertx.vertx();

        JsonObject config = new JsonObject();
        config.put("host", "localhost")
                .put("port", 27017)
                .put("db_name", "url-shortener-app");

        Async async = context.async();
        urlRepository = URLRepository.create(vertx, new JsonObject().put("datasource", config));
        vertx.setTimer(1000, id -> async.complete());
    }

    @After
    public void tearDown(TestContext context) {
        vertx.close(context.asyncAssertSuccess());
    }

    @Test
    @DisplayName("Find all URLs Test")
    public void findAllTest(TestContext context) {
        LOGGER.info("Inside findAllTest");

        Async async = context.async();

        urlRepository.findAll(ar -> {
            if(ar.failed()) context.asyncAssertFailure();

            List<URLData> urlDataList = ar.result();

            context.assertFalse(urlDataList.isEmpty());
            async.complete();
        });
        async.awaitSuccess();
    }
}

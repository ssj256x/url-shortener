package io.urlshortener.repository;

import io.urlshortener.dao.URLRepository;
import io.urlshortener.model.URLData;
import io.urlshortener.verticles.URLRepositoryVerticle;
import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.unit.Async;
import io.vertx.ext.unit.TestContext;
import io.vertx.ext.unit.junit.VertxUnitRunner;
import lombok.extern.log4j.Log4j2;
import org.junit.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.runner.RunWith;

import java.sql.Timestamp;
import java.util.List;

/**
 * Test class for {@link URLRepository}
 * TODO : write test cases for failure scneario
 */
@Log4j2
@RunWith(VertxUnitRunner.class)
public class UrlRepositoryImplTest {

    private static Vertx vertx;
    private static URLRepository urlRepository;

    /**
     * This URLId will be used to test insert, update and delete.
     */
    private static URLData testURLData;

    @BeforeClass
    public static void setUpBeforeClass(TestContext context) {
        vertx = Vertx.vertx();

        JsonObject config = new JsonObject();
        config.put("host", "localhost")
                .put("port", 27017)
                .put("db_name", "url-shortener-app");

        Async async = context.async();

        vertx.deployVerticle(new URLRepositoryVerticle(),
                new DeploymentOptions().setConfig(new JsonObject().put("datasource", config)),
                context.asyncAssertSuccess(ar ->
                        urlRepository = URLRepository.createProxy(vertx, URLRepository.SERVICE_ADDRESS)
                ));

        vertx.setTimer(1000, id -> async.complete());
    }

    @AfterClass
    public static void tearDown(TestContext context) {

        LOGGER.info("Performing cleanup");
        urlRepository.delete(testURLData.getUrlId(), context.asyncAssertSuccess(deletedURLData ->
                LOGGER.info("Deleted Record after failure : {}", deletedURLData)
        ));

        vertx.close(context.asyncAssertSuccess());
    }

    @Before
    public void beforeEach(TestContext context) {
        vertx.exceptionHandler(context.exceptionHandler());
    }

    @Test
    @DisplayName("Save a URL test")
    public void saveUrlTest(TestContext context) {
        LOGGER.info("Running test for saveUrlTest");

        /*
            This testURLData object will be used to perform a CRUD operation on the DB
            TODO : perform all tests by setting up data first and then deleting them after tests succeed
         */
        testURLData = new URLData();
        testURLData.setUrlId("TestURLId");
        testURLData.setUrl("www.website.com/path");
        testURLData.setCreatedOn(new Timestamp(System.currentTimeMillis()).toString());
        testURLData.setUser("testUser");

        Async async = context.async();

        /*
            Here the CRUD Operations are tested using a nested callback pattern.
         */

        // Create a URLData

        urlRepository.save(testURLData, context.asyncAssertSuccess(urlId -> {

            LOGGER.info("Result for saved: " + urlId);
            context.assertEquals(urlId, testURLData.getUrlId());

            // Read the created URLData
            urlRepository.findById(testURLData.getUrlId(), context.asyncAssertSuccess(fetchedURLData -> {

                LOGGER.info("Result for findById: " + fetchedURLData);
                context.assertEquals(fetchedURLData, testURLData);

                // Update the created URLData
                String updatedUrl = "www.updated.website.com/path";
                testURLData.setUrl(updatedUrl);
                urlRepository.update(testURLData, context.asyncAssertSuccess(updatedURLData -> {

                    // Delete the created URLData
                    urlRepository.delete(testURLData.getUrlId(), context.asyncAssertSuccess(deletedURLData -> {

                        context.assertEquals(deletedURLData.getUrl(), updatedUrl);

                        LOGGER.info("Result for deleteUrlTest: " + deletedURLData);
                        context.assertEquals(deletedURLData, testURLData);

                        async.complete();
                    }));
                }));
            }));
        }));
        async.awaitSuccess();
    }

    @Test
    @DisplayName("Find all URLs Test")
    public void findAllTest(TestContext context) {
        LOGGER.info("Running test for findAllTest");

        Async async = context.async();

        urlRepository.findAll(ar -> {
            if (ar.failed()) {
                context.asyncAssertFailure();
                return;
            }

            List<URLData> urlDataList = ar.result();
            LOGGER.info("Fetched List : {}", urlDataList);
            context.assertFalse(urlDataList.isEmpty());
            async.complete();
        });
        async.awaitSuccess();
    }

    @Test
    @DisplayName("Find multiple URLs Test")
    public void findMultipleTest(TestContext context) {
        LOGGER.info("Running test for findMultipleTest");

        Async async = context.async();
        String user = "ssj256x";

        urlRepository.findByUser(user, ar -> {
            if (ar.failed()) {
                context.asyncAssertFailure();
                return;
            }

            List<URLData> urlDataList = ar.result();
            LOGGER.debug("Fetched List : {}", urlDataList);

            context.assertFalse(urlDataList.isEmpty());

            urlDataList.forEach(urlData -> context.assertEquals(urlData.getUser(), user));

            async.complete();
        });
        async.awaitSuccess();
    }
}

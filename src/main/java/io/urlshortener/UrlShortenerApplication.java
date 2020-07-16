package io.urlshortener;

import io.vertx.core.AbstractVerticle;

public class UrlShortenerApplication extends AbstractVerticle {

    public static void main(String[] args) {

        UrlShortenerApplication app = new UrlShortenerApplication();

        try {
            app.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void start() throws Exception {
        super.start();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
    }
}

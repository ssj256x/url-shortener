package io.urlshortener.util;

/**
 * Generic Enums used around the Application
 */
public enum GenericConstants {

    ACTIVE_PROFILE("profiles.active"),
    LOGGER_DELEGATE_FACTORY("vertx.logger-delegate-factory-class-name"),
    LOG4J2_LOG_FACTORY("io.vertx.core.logging.Log4j2LogDelegateFactory"),
    VERTX_CONFIG_PATH("vertx-config-path"),
    HTTP_PORT("http.port"),
    HTTP_ADDRESS("http.address");

    private final String value;
    GenericConstants(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}

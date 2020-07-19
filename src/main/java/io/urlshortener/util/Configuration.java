package io.urlshortener.util;

import lombok.extern.log4j.Log4j2;
import org.apache.commons.lang3.StringUtils;

/**
 * This class has functions for all the different configurations that are to be used throughout the application
 */
@Log4j2
public class Configuration {

    /**
     * Loads basic configurations for the Application
     */

    public void initConfig() {

        LOGGER.debug("Loading Configuration");

        String activeProfile = System.getProperty(GenericConstants.ACTIVE_PROFILE.getValue());

        // TODO : add check if the passed profile is inactive
        if(StringUtils.isBlank(activeProfile)) {
            activeProfile = Environment.E1.getEnv();
        }

        System.setProperty(GenericConstants.LOGGER_DELEGATE_FACTORY.getValue(), GenericConstants.LOG4J2_LOG_FACTORY.getValue());
        System.setProperty(GenericConstants.VERTX_CONFIG_PATH.getValue(), "application.json");
        System.setProperty(GenericConstants.ACTIVE_PROFILE.getValue(), activeProfile);

        LOGGER.info("Configurations Loaded Successfully");
    }

    public void configureJsonMapper() {

    }
}

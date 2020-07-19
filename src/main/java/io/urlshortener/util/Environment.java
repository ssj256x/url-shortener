package io.urlshortener.util;

/**
 * Enum for storing Environment Constants
 */
public enum Environment {

    E0("e0"),
    E1("e1"),
    E2("e2"),
    E3("e3");

    Environment(String value) {
        this.value = value;
    }

    String value;

    public String getEnv() {
       return this.value;
    }
}

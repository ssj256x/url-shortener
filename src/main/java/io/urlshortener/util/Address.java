package io.urlshortener.util;

/**
 * Enum for storing addresses for various services
 */
public enum Address {
    OPEN_API("weeny-url-shortener.app");

    private final String addr;

    Address(String addr) {
        this.addr = addr;
    }

    public String value() {
        return addr;
    }
}

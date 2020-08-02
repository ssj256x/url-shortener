package io.urlshortener.util;

/**
 * Enum for storing addresses for various services
 */
public enum Address {
    OPEN_API("weeny-url-shortener.app");

    private String address;

    Address(String address) {
        this.address = address;
    }

    public String value() {
        return address;
    }
}

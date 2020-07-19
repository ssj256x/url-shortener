package io.urlshortener.util;

public enum ApiHeaders {
    CONTENT_TYPE("Content-Type");

    String value;
    ApiHeaders(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }
}

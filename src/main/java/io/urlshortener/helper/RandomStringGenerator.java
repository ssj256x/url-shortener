package io.urlshortener.helper;

public class RandomStringGenerator {

    private static final String ALPHANUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890";

    public String generateAlphaNumericString(int length) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < length; i++) {
            int index = (int) (ALPHANUMERIC_STRING.length() * Math.random());
            sb.append(ALPHANUMERIC_STRING.charAt(index));
        }
        return new String(sb);
    }
}

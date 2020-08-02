package io.urlshortener.model;

import lombok.*;

@Data
public class ErrorResponse {
    private String errorCode;
    private String errorText;
}

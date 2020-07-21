package io.urlshortener.model;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class URLCreatedResponse {
    private String generatedId;
    private String shortenedUrl;
    private String createdOn;
}
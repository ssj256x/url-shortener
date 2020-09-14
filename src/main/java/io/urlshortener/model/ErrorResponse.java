package io.urlshortener.model;

import io.vertx.core.json.JsonObject;
import lombok.*;

@Setter
@Getter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {
    private String errorCode;
    private String errorText;

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        json.put("errorCode", this.errorCode);
        json.put("errorText", this.errorText);
        return json;
    }
}

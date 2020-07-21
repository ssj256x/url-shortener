package io.urlshortener.model;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Data Model class for the MongoDB Collection url-data
 *
 * TODO : find a way to override the toString property of lombok in this package
 */

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@DataObject
public class URLData {
    public static final String DB_COLLECTION = "url-data";

    private String urlId;
    private String url;
    private String user;
    private Info info;
    private String createdOn;

    public URLData(JsonObject json) {
        this.url = json.getString("url");
        this.urlId = json.getString("urlId");
        this.user = json.getString("user");
        this.createdOn = json.getString("createdOn");
        if(info != null) this.info = Json.decodeValue(json.getJsonObject("info").toBuffer(), Info.class);
    }

    @Override
    public String toString() {
        return Json.encodePrettily(this);
    }

    public JsonObject toJson() {
        return JsonObject.mapFrom(this);
    }
}

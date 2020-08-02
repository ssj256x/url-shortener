package io.urlshortener.model;

import io.vertx.codegen.annotations.DataObject;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonObject;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 * Data Model class for the MongoDB Collection url-data
 */

@AllArgsConstructor
@NoArgsConstructor
@DataObject(generateConverter = true)
public class URLData {
    public static final String DB_COLLECTION = "url-data";

    private String urlId;
    private String url;
    private String user;
    private String createdOn;

    public URLData(JsonObject json) {
        URLDataConverter.fromJson(json, this);
    }

    @Override
    public String toString() {
        return Json.encodePrettily(this);
    }

    public JsonObject toJson() {
        JsonObject json = new JsonObject();
        URLDataConverter.toJson(this, json);
        return json;
    }

    public String getUrlId() {
        return urlId;
    }

    public URLData setUrlId(String urlId) {
        this.urlId = urlId;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public URLData setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUser() {
        return user;
    }

    public URLData setUser(String user) {
        this.user = user;
        return this;
    }

    public String getCreatedOn() {
        return createdOn;
    }

    public URLData setCreatedOn(String createdOn) {
        this.createdOn = createdOn;
        return this;
    }
}

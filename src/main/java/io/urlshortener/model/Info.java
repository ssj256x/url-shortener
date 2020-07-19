package io.urlshortener.model;

import io.vertx.core.json.Json;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Info {
    private List<String> tags;

    @Override
    public String toString() {
        return Json.encodePrettily(this);
    }
}

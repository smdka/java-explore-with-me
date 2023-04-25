package ru.practicum.ewm;

import lombok.NoArgsConstructor;
import lombok.Value;

@Value
@NoArgsConstructor(force = true)
public class ViewStats {
    String app;
    String uri;
    Long hits;

    public ViewStats(String uri, String app, Long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }
}

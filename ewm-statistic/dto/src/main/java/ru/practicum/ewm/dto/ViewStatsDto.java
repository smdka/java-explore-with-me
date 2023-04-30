package ru.practicum.ewm.dto;

import lombok.Value;

@Value
public class ViewStatsDto {
    String app;
    String uri;
    Long hits;

    private ViewStatsDto() {
        this.app = null;
        this.uri = null;
        this.hits = null;
    }

    public ViewStatsDto(String app, String uri, Long hits) {
        this.app = app;
        this.uri = uri;
        this.hits = hits;
    }
}

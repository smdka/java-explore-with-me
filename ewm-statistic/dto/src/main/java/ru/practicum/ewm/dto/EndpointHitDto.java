package ru.practicum.ewm.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Value;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Value
public class EndpointHitDto {
    @NotBlank
    String app;

    @NotBlank
    String uri;

    @NotBlank
    String ip;

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    LocalDateTime timestamp;

    public EndpointHitDto(@NotBlank String app, @NotBlank String uri, @NotBlank String ip, @NotNull LocalDateTime timestamp) {
        this.app = app;
        this.uri = uri;
        this.ip = ip;
        this.timestamp = timestamp;
    }

    private EndpointHitDto() {
        this.app = null;
        this.uri = null;
        this.ip = null;
        this.timestamp = null;
    }
}
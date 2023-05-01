package ru.practicum.ewm.compilations.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

@Getter
@Setter
public class CompilationEventId implements Serializable {
    private Long compilationId;
    private Long eventId;
}
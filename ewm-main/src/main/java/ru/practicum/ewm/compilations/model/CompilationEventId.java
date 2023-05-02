package ru.practicum.ewm.compilations.model;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.io.Serializable;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class CompilationEventId implements Serializable {
    Long compilationId;
    Long eventId;
}
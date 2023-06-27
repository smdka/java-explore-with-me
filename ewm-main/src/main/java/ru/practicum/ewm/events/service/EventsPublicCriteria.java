package ru.practicum.ewm.events.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.events.dto.State;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EventsPublicCriteria {
    int from = 0;
    int size = 10;
    State state;
    String text;
    List<Long> categories;
    Boolean paid;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    SortBy sort;
    boolean onlyAvailable = false;
}

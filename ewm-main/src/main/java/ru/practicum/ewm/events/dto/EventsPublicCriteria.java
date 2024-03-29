package ru.practicum.ewm.events.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.events.service.SortBy;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
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

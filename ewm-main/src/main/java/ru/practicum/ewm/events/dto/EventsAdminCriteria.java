package ru.practicum.ewm.events.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class EventsAdminCriteria {
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    List<Long> users;
    List<State> states;
    List<Long> categories;
    int from = 0;
    int size = 10;
}
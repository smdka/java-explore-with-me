package ru.practicum.ewm.events.service;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import ru.practicum.ewm.events.dto.SortBy;
import ru.practicum.ewm.events.dto.State;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class GetPublicEventsArgs {
    Integer from;
    Integer size;
    State state;
    String text;
    List<Long> categories;
    Boolean paid;
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    SortBy sort;
    boolean onlyAvailable;
    String ip;
    String url;
}

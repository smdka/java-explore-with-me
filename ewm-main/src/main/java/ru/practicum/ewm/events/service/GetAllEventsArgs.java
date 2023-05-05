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
public class GetAllEventsArgs {
    LocalDateTime rangeStart;
    LocalDateTime rangeEnd;
    List<Long> users;
    List<State> states;
    List<Long> categories;
    Integer from;
    Integer size;
}

package ru.practicum.ewm.service;

import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.ViewStats;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EndpointHitService {
    void add(EndpointHitDto endpointHitDto);

    Collection<ViewStats> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}

package ru.practicum.ewm.service;

import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EndpointHitService {
    void add(EndpointHitDto endpointHitDto);

    Collection<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique);
}

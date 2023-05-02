package ru.practicum.ewm.events.dto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

public enum SortBy implements SortStrategy<EventDto> {
    EVENT_DATE(eventDtos -> {
        List<EventDto> dtos = new ArrayList<>(eventDtos);
        dtos.sort(Comparator.comparing(EventDto::getEventDate).reversed());
        return dtos;
    }),

    VIEWS(eventDtos -> {
        List<EventDto> dtos = new ArrayList<>(eventDtos);
        dtos.sort(Comparator.comparing(EventDto::getViews).reversed());
        return dtos;
    });

    private final SortStrategy<EventDto> sortStrategy;

    SortBy(final SortStrategy<EventDto> sortStrategy) {
        this.sortStrategy = sortStrategy;
    }

    @Override
    public Collection<EventDto> sort(Collection<EventDto> eventDtos) {
        return sortStrategy.sort(eventDtos);
    }
}

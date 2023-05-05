package ru.practicum.ewm.events.service;

import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.events.dto.EventDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.users.dto.UserDto;

import java.util.Collection;

public interface EventService {
    EventDto update(Long eventId, NewEventDto newEventDto);

    Collection<EventDto> getAll(GetAllEventsArgs getAllEventsArgs);

    EventDto add(NewEventDto newEventDto, UserDto userDto, CategoryDto categoryDto);

    Collection<EventDto> getByUserId(Long userId, Integer from, Integer size);

    EventDto getByUserIdAndEventId(Long userId, Long eventId);

    EventDto updateByUserIdAndEventId(Long userId, Long eventId, NewEventDto newEventDto);

    Collection<EventDto> getPublicEvents(GetPublicEventsArgs getPublicEventsArgs);

    EventDto getPublicEventById(Long eventId, String ip, String url);
}

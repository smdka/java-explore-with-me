package ru.practicum.ewm.events.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.events.dto.EventDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.location.dto.LocationDto;
import ru.practicum.ewm.location.dto.NewLocationDto;
import ru.practicum.ewm.location.model.Location;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.dto.UserShortDto;
import ru.practicum.ewm.users.model.User;

import java.util.Collection;
import java.util.Map;

import static java.util.stream.Collectors.toList;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class EventMapper {
    public static Event toModel(NewEventDto newEventDto,
                                LocationDto locationDto,
                                UserDto userDto,
                                CategoryDto categoryDto) {
        Event event = new Event();
        Category category = new Category();
        Location location = new Location();
        User user = new User();

        user.setId(userDto.getId());
        user.setName(userDto.getName());
        user.setEmail(userDto.getEmail());

        location.setId(locationDto.getId());
        location.setLat(locationDto.getLat());
        location.setLon(locationDto.getLon());

        category.setId(categoryDto.getId());
        category.setName(categoryDto.getName());

        event.setAnnotation(newEventDto.getAnnotation());
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(newEventDto.getEventDate());
        event.setPaid(newEventDto.getPaid());
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setRequestModeration(newEventDto.getRequestModeration());
        event.setTitle(newEventDto.getTitle());
        event.setState(newEventDto.getStateAction());

        event.setLocation(location);
        event.setCategory(category);
        event.setInitiator(user);

        return event;
    }

    public static EventDto toDto(Event event) {
        CategoryDto categoryDto = new CategoryDto(event.getCategory().getId(), event.getCategory().getName());
        UserShortDto userShortDto = new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName());
        NewLocationDto newLocationDto = new NewLocationDto(event.getLocation().getLat(), event.getLocation().getLon());

        return new EventDto(
                event.getId(),
                event.getAnnotation(),
                categoryDto,
                event.getConfirmedRequests(),
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                userShortDto,
                newLocationDto,
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                0L);
    }

    public static EventDto toDto(Event event, Long hits) {
        CategoryDto categoryDto = new CategoryDto(event.getCategory().getId(), event.getCategory().getName());
        UserShortDto userShortDto = new UserShortDto(event.getInitiator().getId(), event.getInitiator().getName());
        NewLocationDto newLocationDto = new NewLocationDto(event.getLocation().getLat(), event.getLocation().getLon());

        return new EventDto(
                event.getId(),
                event.getAnnotation(),
                categoryDto,
                event.getConfirmedRequests(),
                event.getCreatedOn(),
                event.getDescription(),
                event.getEventDate(),
                userShortDto,
                newLocationDto,
                event.getPaid(),
                event.getParticipantLimit(),
                event.getPublishedOn(),
                event.getRequestModeration(),
                event.getState(),
                event.getTitle(),
                hits);
    }

    public static Collection<EventDto> toDto(Collection<Event> events, Map<String, Long> eventViews) {
        return events.stream().map(event -> {
            Long hits = eventViews.get(String.format("/events/%s", event.getId()));

            return toDto(event, hits != null ? hits : 0);
        }).collect(toList());
    }

    public static Collection<EventDto> toDto(Collection<Event> events) {
        return events.stream()
                .map(EventMapper::toDto)
                .collect(toList());
    }
}

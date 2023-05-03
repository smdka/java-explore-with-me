package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.service.CategoryService;
import ru.practicum.ewm.events.dto.EventDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.service.EventService;
import ru.practicum.ewm.locations.dto.LocationDto;
import ru.practicum.ewm.locations.dto.NewLocationDto;
import ru.practicum.ewm.locations.service.LocationService;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.service.UserService;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("users")
public class EventsPrivateController {

    private final EventService eventService;

    private final LocationService locationService;

    private final UserService userService;

    private final CategoryService categoryService;

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto post(@PathVariable Long userId,
                         @RequestBody @Valid NewEventDto newEventDto) {
        log.info("POST /users/{}/events", userId);
        log.info("Request body: {}", newEventDto);

        NewLocationDto newLocationDto = new NewLocationDto(newEventDto.getLocation().getLat(), newEventDto.getLocation().getLon());
        LocationDto locationDto = locationService.add(newLocationDto);
        UserDto userDto = new ArrayList<>(userService.getAll(List.of(userId), 0, 1)).get(0);
        CategoryDto categoryDto = categoryService.getById(newEventDto.getCategory(), 0, 1);

        return eventService.add(newEventDto, locationDto, userDto, categoryDto);
    }

    @GetMapping("/{userId}/events")
    public Collection<EventDto> getByUserId(@PathVariable Long userId,
                                            @RequestParam(defaultValue = "0") Integer from,
                                            @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /users/{}/events?from={}&size={}", userId, from, size);
        return eventService.getByUserId(userId, from, size);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventDto getByUserIdAndEventId(@PathVariable Long userId,
                                          @PathVariable Long eventId) {
        log.info("GET /users/{}/events/{}", userId, eventId);
        return eventService.getByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventDto updateByUserIdAndEventId(@PathVariable Long userId,
                                             @PathVariable Long eventId,
                                             @RequestBody NewEventDto newEventDto) {
        log.info("PATCH /users/{}/events/{}", userId, eventId);
        log.info("Request body: {}", newEventDto);
        return eventService.updateByUserIdAndEventId(userId, eventId, newEventDto);
    }
}

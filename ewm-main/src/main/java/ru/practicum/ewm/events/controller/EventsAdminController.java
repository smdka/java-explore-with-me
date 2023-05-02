package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.EventDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.State;
import ru.practicum.ewm.events.service.GetAllEventsArgs;
import ru.practicum.ewm.events.service.EventService;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("admin/events")
public class EventsAdminController {
    private final EventService service;

    @PatchMapping("/{eventId}")
    public EventDto patchByUserIdAndEventId(@PathVariable Long eventId,
                                            @RequestBody(required = false) NewEventDto newEventDto) {
        log.info("POST /admin/events/{}", eventId);
        log.info("Request body: {}", newEventDto);
        return service.updateByEventId(eventId, newEventDto);
    }

    @GetMapping
    public Collection<EventDto> getAll(@RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                       @RequestParam(required = false) List<Long> users,
                                       @RequestParam(required = false) List<State> states,
                                       @RequestParam(required = false) List<Long> categories,
                                       @RequestParam(defaultValue = "0") Integer from,
                                       @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /admin/events?rangeStart={}&rangeEnd={}&users={}&states={}&categories={}&from={}&size={}",
                rangeStart, rangeEnd, users, states, categories, from, size);
        return service.getAll(new GetAllEventsArgs(rangeStart, rangeEnd, users, states, categories, from, size));
    }
}

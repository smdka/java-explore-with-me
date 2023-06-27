package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.events.dto.EventDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.service.EventService;
import ru.practicum.ewm.events.service.EventsAdminCriteria;

import java.util.Collection;

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
        return service.update(eventId, newEventDto);
    }

    @GetMapping
    public Collection<EventDto> getAll(EventsAdminCriteria eventsAdminCriteria) {
        log.info("GET /admin/events?{}", eventsAdminCriteria);
        return service.getAll(eventsAdminCriteria);
    }
}

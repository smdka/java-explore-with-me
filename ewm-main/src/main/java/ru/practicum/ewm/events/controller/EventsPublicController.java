package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.events.dto.EventDto;
import ru.practicum.ewm.events.service.EventService;
import ru.practicum.ewm.events.service.EventsPublicCriteria;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("events")
public class EventsPublicController {
    private final EventService eventService;

    @GetMapping
    public Collection<EventDto> getAll(HttpServletRequest request, EventsPublicCriteria eventsPublicCriteria) {
        log.info("GET /events?{}", eventsPublicCriteria);

        String ip = request.getRemoteAddr();
        String url = request.getRequestURI();

        return eventService.getPublicEvents(eventsPublicCriteria, ip, url);
    }

    @GetMapping("/{eventId}")
    public EventDto getById(@PathVariable Long eventId,
                            HttpServletRequest request) {
        log.info("GET /events/{}", eventId);

        String ip = request.getRemoteAddr();
        String url = request.getRequestURI();

        return eventService.getPublicEventById(eventId, ip, url);
    }
}

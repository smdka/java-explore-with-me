package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.events.dto.EventDto;
import ru.practicum.ewm.events.dto.SortBy;
import ru.practicum.ewm.events.dto.State;
import ru.practicum.ewm.events.service.EventService;
import ru.practicum.ewm.events.service.GetPublicEventsArgs;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("events")
public class EventsPublicController {
    private final EventService eventService;

    @GetMapping
    public Collection<EventDto> getAll(@RequestParam(required = false) String text,
                                       @RequestParam(required = false) List<Long> categories,
                                       @RequestParam(required = false) Boolean paid,
                                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
                                       @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
                                       @RequestParam(defaultValue = "false") Boolean onlyAvailable,
                                       @RequestParam(required = false) SortBy sort,
                                       @RequestParam(defaultValue = "0") Integer from,
                                       @RequestParam(defaultValue = "10") Integer size,
                                       HttpServletRequest request) {
        log.info("GET /events?text={}&categories={}&paid={}&rangeStart={}&rangeEnd={}&onlyAvailable={}&sort={}&from={}&size={}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        String ip = request.getRemoteAddr();
        String url = request.getRequestURI();

        return eventService.getPublicEvents(new GetPublicEventsArgs(
                from, size, State.PUBLISHED, text, categories, paid, rangeStart, rangeEnd, sort, onlyAvailable, ip, url));
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

package ru.practicum.ewm.events.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewm.events.dto.EventDto;
import ru.practicum.ewm.events.dto.EventsPublicCriteria;
import ru.practicum.ewm.events.service.EventService;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

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
    public EventDto getById(@PathVariable Long eventId, HttpServletRequest request) {
        log.info("GET /events/{}", eventId);

        String ip = request.getRemoteAddr();
        String url = request.getRequestURI();

        EventDto response = eventService.getPublicEventById(eventId, ip, url);

        response.add(linkTo(methodOn(EventsAdminController.class)
                        .getAll(null))
                        .withRel("getAllEvents"),
                linkTo(methodOn(EventsAdminController.class)
                        .patchByUserIdAndEventId(eventId, null))
                        .withRel("updateEvent"),
                linkTo(methodOn(EventsPrivateController.class)
                        .getByUserId(null, null, null))
                        .withRel("getEventByUserId"),
                linkTo(methodOn(EventsPrivateController.class)
                        .getByUserIdAndEventId(null, null))
                        .withRel("getEventByUserIdAndEventId"),
                linkTo(methodOn(EventsPrivateController.class)
                        .post(null, null))
                        .withRel("createEvent"),
                linkTo(methodOn(EventsPrivateController.class)
                        .updateByUserIdAndEventId(null, eventId, null))
                        .withRel("updateEvent"),
                linkTo(methodOn(EventsPublicController.class)
                        .getAll(request, null))
                        .withRel("getAllEvents"),
                linkTo(methodOn(EventsPublicController.class)
                        .getById(eventId, request))
                        .withSelfRel());

        return response;
    }
}

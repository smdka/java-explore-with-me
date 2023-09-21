package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.stream.messaging.Source;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatisticClient;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.events.dto.EventDto;
import ru.practicum.ewm.events.dto.EventsAdminCriteria;
import ru.practicum.ewm.events.dto.EventsPublicCriteria;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.State;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.exceptions.OperationException;
import ru.practicum.ewm.locations.dto.LocationDto;
import ru.practicum.ewm.locations.dto.NewLocationDto;
import ru.practicum.ewm.locations.repository.LocationRepository;
import ru.practicum.ewm.locations.service.LocationMapper;
import ru.practicum.ewm.requests.model.RequestStat;
import ru.practicum.ewm.requests.repository.RequestRepository;
import ru.practicum.ewm.users.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final RequestRepository requestRepository;

    private final EventRepository eventRepository;

    private final StatisticClient statisticClient;

    private final LocationRepository locationRepository;

    private final Source source;

    @Value("${spring.application.name}")
    private String appName;

    public static final String EVENT_NOT_FOUND_MSG = "Событие с id=%s не найдено";
    private static final String OPERATION_EXCEPTION_MSG = "Field: eventDate. Error: должно содержать дату, которая еще не наступила. Value: %s";
    private static final String EVENT_UPDATE_PUBLISHED_MSG = "Невозможно обновить событие со статусом %s";
    private static final String EVENT_STATE_EXCEPTION_MSG = "Невозможно опубликовать событие со статусом %s";
    private static final String EVENT_CANCEL_EXCEPTION_MSG = "Невозможно отменить событие со статусом %s";

    @Override
    @Transactional
    public EventDto update(Long eventId, NewEventDto newEventDto) {
        return eventRepository.findById(eventId)
                .map(event -> Optional.ofNullable(newEventDto)
                        .map(eventDto -> getUpdatedEventDto(event, eventDto))
                        .orElse(EventMapper.toDto(event)))
                .orElseThrow(() -> new NotFoundException(String.format(EVENT_NOT_FOUND_MSG, eventId)));
    }

    private EventDto getUpdatedEventDto(Event event, NewEventDto eventDto) {
        validateEventDate(eventDto.getEventDate().plusHours(1), event.getCreatedOn());
        updateEventState(event, eventDto);
        updateEventDetails(event, eventDto);
        return getEventDtoWithViews(event);
    }

    private void validateEventDate(LocalDateTime eventDate, LocalDateTime limit) {
        if (eventDate.isBefore(limit)) {
            throw new OperationException(String.format(OPERATION_EXCEPTION_MSG, eventDate));
        }
    }

    private void updateEventState(Event event, NewEventDto newEventDto) {
        State stateAction = newEventDto.getStateAction();

        if (stateAction == null && event.getState().equals(State.PUBLISHED)) {
            throw new OperationException(String.format(EVENT_UPDATE_PUBLISHED_MSG, event.getState()));
        }

        if (stateAction == null) {
            return;
        }

        switch (stateAction) {
            case CANCEL_REVIEW:
                event.setState(State.CANCELED);
                break;
            case SEND_TO_REVIEW:
                event.setState(State.PENDING);
                break;
            case REJECT_EVENT:
                if (event.getState().equals(State.PUBLISHED)) {
                    throw new OperationException(String.format(EVENT_CANCEL_EXCEPTION_MSG, event.getState()));
                }
                event.setState(State.CANCELED);
                break;
            case PUBLISH_EVENT:
                if (event.getState().equals(State.PUBLISHED) || event.getState().equals(State.CANCELED)) {
                    throw new OperationException(String.format(EVENT_STATE_EXCEPTION_MSG, event.getState()));
                }
                if (event.getState().equals(State.PENDING)) {
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    event.setRequestModeration(true);
                }
                break;
            default:
                break;
        }
    }

    private void updateEventDetails(Event event, NewEventDto newEventDto) {
        event.setParticipantLimit(newEventDto.getParticipantLimit());
        event.setTitle(newEventDto.getTitle());
        event.setPaid(newEventDto.getPaid());
        event.setDescription(newEventDto.getDescription());
        event.setEventDate(newEventDto.getEventDate());
        event.setAnnotation(newEventDto.getAnnotation());
    }

    @Override
    public Collection<EventDto> getAll(EventsAdminCriteria eventsAdminCriteria) {
        Pageable pageable = PageRequest.of(eventsAdminCriteria.getFrom(), eventsAdminCriteria.getSize());

        List<Event> events = eventRepository.findAll(eventsAdminCriteria.getRangeStart(), eventsAdminCriteria.getRangeEnd(),
                eventsAdminCriteria.getUsers(), eventsAdminCriteria.getStates(), eventsAdminCriteria.getCategories(), pageable);

        return getEventDtosWithViews(events);
    }

    private List<EventDto> getEventDtosWithViews(List<Event> events) {
        Map<String, Long> eventViewsMap = getEventViewsMap(getEventsViewsList(events));
        return EventMapper.toDto(events, eventViewsMap);
    }

    @Override
    @Transactional
    public EventDto add(NewEventDto newEventDto, UserDto userDto, CategoryDto categoryDto) {
        NewLocationDto newLocationDto = new NewLocationDto(newEventDto.getLocation().getLat(), newEventDto.getLocation().getLon());
        LocationDto locationDto = LocationMapper.MAP.toDto(locationRepository.save(LocationMapper.MAP.toModel(newLocationDto)));

        validateEventDate(newEventDto.getEventDate(), LocalDateTime.now().plusHours(2));

        Event event = EventMapper.toModel(newEventDto, locationDto, userDto, categoryDto);

        event.setState(State.PENDING);

        return EventMapper.toDto(eventRepository.save(event));
    }

    @Override
    public Collection<EventDto> getByUserId(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        return getEventDtosWithViews(events);
    }

    @Override
    public EventDto getByUserIdAndEventId(Long userId, Long eventId) {
        Event event = getEventByInitiatorIdAndEventId(userId, eventId);
        return getEventDtoWithViews(event);
    }

    @Override
    @Transactional
    public EventDto updateByUserIdAndEventId(Long userId, Long eventId, NewEventDto newEventDto) {
        validateEventDate(newEventDto.getEventDate(), LocalDateTime.now().plusHours(2));

        Event event = getEventByInitiatorIdAndEventId(userId, eventId);
        updateEventState(event, newEventDto);

        return getEventDtoWithViews(event);
    }

    @Override
    public Collection<EventDto> getPublicEvents(EventsPublicCriteria eventsPublicCriteria, String ip, String url) {
        sendNewHitMessage(ip, url);

        Pageable pageable = PageRequest.of(eventsPublicCriteria.getFrom(), eventsPublicCriteria.getSize());
        List<Event> events = eventRepository.findAllPublic(
                pageable,
                eventsPublicCriteria.getState(),
                eventsPublicCriteria.getText(),
                eventsPublicCriteria.getCategories(),
                eventsPublicCriteria.getPaid(),
                eventsPublicCriteria.getRangeStart(),
                eventsPublicCriteria.getRangeEnd());

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(toList());

        List<RequestStat> requestStats = new ArrayList<>(requestRepository.getRequestsStats(eventIds));

        List<EventDto> eventDtosWithViews = getEventDtosWithViews(events);

        List<EventDto> sortedEvents = new ArrayList<>(sortEvents(eventsPublicCriteria.getSort(), eventDtosWithViews));

        return filterEventsByAvailable(sortedEvents, requestStats, eventsPublicCriteria.isOnlyAvailable());
    }

    @Override
    public EventDto getPublicEventById(Long eventId, String ip, String url) {
        sendNewHitMessage(ip, url);
        
        return eventRepository.findEventByIdAndState(eventId, State.PUBLISHED)
                .map(this::getEventDtoWithViews)
                .orElseThrow(() -> new NotFoundException(String.format(EVENT_NOT_FOUND_MSG, eventId)));
    }

    private EventDto getEventDtoWithViews(Event event) {
        Map<String, Long> eventViewsMap = getEventViewsMap(getEventsViewsList(List.of(event)));
        return EventMapper.toDto(event, eventViewsMap);
    }

    private Collection<ViewStatsDto> getEventsViewsList(List<Event> events) {
        List<String> eventUris = events
                .stream()
                .map(e -> String.format("/events/%s", e.getId()))
                .collect(toList());

        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String start = LocalDateTime.now().minusYears(2).format(customFormatter);
        String end = LocalDateTime.now().plusYears(2).format(customFormatter);

        return statisticClient.getStats(start, end, eventUris, false);
    }

    private Map<String, Long> getEventViewsMap(Collection<ViewStatsDto> viewStatDtosList) {
        return viewStatDtosList.stream()
                .collect(toMap(ViewStatsDto::getUri, ViewStatsDto::getHits));
    }

    private void sendNewHitMessage(String ip, String url) {
        source.output().send(MessageBuilder.withPayload(new EndpointHitDto(appName, url, ip, LocalDateTime.now())).build());
    }

    private Collection<EventDto> sortEvents(SortBy sortBy, List<EventDto> eventDtos) {
        return sortBy == null ? eventDtos : sortBy.sort(eventDtos);
    }

    private Collection<EventDto> filterEventsByAvailable(List<EventDto> eventDtos,
                                                         List<RequestStat> requestStats,
                                                         boolean onlyAvailable) {
        if (!onlyAvailable || requestStats.isEmpty()) {
            return eventDtos;
        }

        Map<Long, Long> requestStatsMap = getRequestStatsMap(requestStats);

        return eventDtos.stream()
                .filter(eventDto -> requestStatsMap.get(eventDto.getId()) < eventDto.getParticipantLimit())
                .collect(toList());
    }

    private Map<Long, Long> getRequestStatsMap(Collection<RequestStat> requestStats) {
        return requestStats.stream()
                .collect(toMap(RequestStat::getEventId, RequestStat::getRequests));
    }

    private Event getEventByInitiatorIdAndEventId(Long userId, Long eventId) {
        return eventRepository.findByInitiatorIdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(String.format(EVENT_NOT_FOUND_MSG, eventId)));
    }

}

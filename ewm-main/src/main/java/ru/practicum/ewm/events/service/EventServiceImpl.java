package ru.practicum.ewm.events.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.StatisticClient;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.events.dto.EventDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.dto.SortBy;
import ru.practicum.ewm.events.dto.State;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.exceptions.OperationException;
import ru.practicum.ewm.locations.dto.LocationDto;
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

    private static final String EVENT_NOT_FOUND_MSG = "Событие с id=%s не найдено";
    private static final String OPERATION_EXCEPTION_MSG = "Field: eventDate. Error: должно содержать дату, " +
            "которая еще не наступила. Value: %s";
    private static final String EVENT_UPDATE_PUBLISHED_MSG = "Невозможно обновить событие со статусом %s";
    private static final String EVENT_STATE_EXCEPTION_MSG = "Невозможно опубликовать событие со статусом %s";
    private static final String EVENT_CANCEL_EXCEPTION_MSG = "Невозможно отменить событие со статусом %s";

    @Override
    @Transactional
    public EventDto update(Long eventId, NewEventDto newEventDto) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(EVENT_NOT_FOUND_MSG, eventId)));

        if (newEventDto == null) {
            return EventMapper.toDto(event);
        }

        validateAdminEventDate(newEventDto.getEventDate(), event);
        updateEventState(event, newEventDto);
        updateEventDetails(event, newEventDto);

        Map<String, Long> eventViewsMap = getEventViewsMap(getEventsViewsList(List.of(event)));

        return EventMapper.toDto(List.of(event), eventViewsMap).get(0);
    }

    @Override
    public Collection<EventDto> getAll(GetAllEventsArgs getAllEventsArgs) {
        Pageable pageable = PageRequest.of(getAllEventsArgs.getFrom(), getAllEventsArgs.getSize());

        List<Event> events = eventRepository.findAll(getAllEventsArgs.getRangeStart(), getAllEventsArgs.getRangeEnd(),
                getAllEventsArgs.getUsers(), getAllEventsArgs.getStates(), getAllEventsArgs.getCategories(), pageable);

        Map<String, Long> eventViewsMap = getEventViewsMap(getEventsViewsList(events));

        return EventMapper.toDto(events, eventViewsMap);
    }

    @Override
    @Transactional
    public EventDto add(NewEventDto newEventDto, LocationDto locationDto, UserDto userDto, CategoryDto categoryDto) {
        validateEventDate(newEventDto.getEventDate(), LocalDateTime.now().plusHours(2));

        Event event = EventMapper.toModel(newEventDto, locationDto, userDto, categoryDto);

        event.setState(State.PENDING);

        return EventMapper.toDto(eventRepository.save(event));
    }

    @Override
    public Collection<EventDto> getByUserId(Long userId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Event> events = eventRepository.findAllByInitiatorId(userId, pageable);
        Map<String, Long> eventViewsMap = getEventViewsMap(getEventsViewsList(events));

        return EventMapper.toDto(events, eventViewsMap);
    }

    @Override
    public EventDto getByUserIdAndEventId(Long userId, Long eventId) {
        Event event = getEventByInitiatorIdAndEventId(userId, eventId);
        Map<String, Long> eventViewsMap = getEventViewsMap(getEventsViewsList(List.of(event)));

        return EventMapper.toDto(List.of(event), eventViewsMap).get(0);
    }

    @Override
    @Transactional
    public EventDto updateByUserIdAndEventId(Long userId, Long eventId, NewEventDto newEventDto) {
        validateEventDate(newEventDto.getEventDate(), LocalDateTime.now().plusHours(2));

        Event event = getEventByInitiatorIdAndEventId(userId, eventId);
        Map<String, Long> eventViewsMap = getEventViewsMap(getEventsViewsList(List.of(event)));

        updateEventState(event, newEventDto);

        return EventMapper.toDto(List.of(event), eventViewsMap).get(0);
    }

    @Override
    public Collection<EventDto> getPublicEvents(GetPublicEventsArgs getPublicEventsArgs) {
        createNewHit(getPublicEventsArgs.getIp(), getPublicEventsArgs.getUrl());

        Pageable pageable = PageRequest.of(getPublicEventsArgs.getFrom(), getPublicEventsArgs.getSize());
        List<Event> events = eventRepository.findAllPublic(
                pageable,
                getPublicEventsArgs.getState(),
                getPublicEventsArgs.getText(),
                getPublicEventsArgs.getCategories(),
                getPublicEventsArgs.getPaid(),
                getPublicEventsArgs.getRangeStart(),
                getPublicEventsArgs.getRangeEnd());

        List<Long> eventIds = events.stream()
                .map(Event::getId)
                .collect(toList());

        List<RequestStat> requestStats = new ArrayList<>(requestRepository.getRequestsStats(eventIds));

        Map<String, Long> eventViewsMap = getEventViewsMap(getEventsViewsList(events));

        List<EventDto> eventDtos = EventMapper.toDto(events, eventViewsMap);

        List<EventDto> sortedEvents = new ArrayList<>(sortEvents(getPublicEventsArgs.getSort(), eventDtos));

        return filterEventsByAvailable(sortedEvents, requestStats, getPublicEventsArgs.isOnlyAvailable());
    }

    @Override
    public EventDto getPublicEventById(Long eventId, String ip, String url) {
        createNewHit(ip, url);

        Event event = getEventByEventIdAndState(eventId);
        Map<String, Long> eventViewsMap = getEventViewsMap(getEventsViewsList(List.of(event)));

        return new ArrayList<>(EventMapper.toDto(List.of(event), eventViewsMap)).get(0);
    }

    private void validateEventDate(LocalDateTime eventDate, LocalDateTime limit) {
        if (eventDate != null && eventDate.isBefore(limit)) {
            String error = String.format(OPERATION_EXCEPTION_MSG, eventDate);
            throw new OperationException(error);
        }
    }

    private void validateAdminEventDate(LocalDateTime eventDate, Event event) {
        if (event.getCreatedOn() != null
                && eventDate != null
                && eventDate.plusHours(1).isBefore(event.getCreatedOn())) {
            String error = String.format(OPERATION_EXCEPTION_MSG, eventDate);
            throw new OperationException(error);
        }
    }

    private void updateEventState(Event event, NewEventDto newEventDto) {
        State stateAction = newEventDto.getStateAction();

        if (stateAction == null && event.getState().equals(State.PUBLISHED)) {
            throw new OperationException(String.format(EVENT_UPDATE_PUBLISHED_MSG,
                    event.getState()));
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

    private Collection<ViewStatsDto> getEventsViewsList(List<Event> events) {
        DateTimeFormatter customFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        List<String> eventUris = events
                .stream()
                .map(e -> String.format("/events/%s", e.getId()))
                .collect(toList());
        String start = LocalDateTime.now().minusYears(2).format(customFormatter);
        String end = LocalDateTime.now().plusYears(2).format(customFormatter);

        return statisticClient.getStats(start, end, eventUris, false);
    }

    private void createNewHit(String ip, String url) {
        String serviceName = "ewm-main-service";

        statisticClient.addHit(new EndpointHitDto(serviceName, url, ip, LocalDateTime.now()));
    }

    private Collection<EventDto> sortEvents(SortBy sortBy, List<EventDto> eventDtos) {
        return sortBy == null ? eventDtos : sortBy.sort(eventDtos);
    }

    private Map<String, Long> getEventViewsMap(Collection<ViewStatsDto> viewStatDtosList) {
        return viewStatDtosList.stream()
                .collect(toMap(ViewStatsDto::getUri, ViewStatsDto::getHits));
    }

    private Map<Long, Long> getRequestStatsMap(Collection<RequestStat> requestStats) {
        return requestStats.stream()
                .collect(toMap(RequestStat::getEventId, RequestStat::getRequests));
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

    private void updateEventDetails(Event event, NewEventDto newEventDto) {
        event.setParticipantLimit(Objects.requireNonNullElse(newEventDto.getParticipantLimit(), event.getParticipantLimit()));
        event.setTitle(Objects.requireNonNullElse(newEventDto.getTitle(), event.getTitle()));
        event.setPaid(Objects.requireNonNullElse(newEventDto.getPaid(), event.getPaid()));
        event.setDescription(Objects.requireNonNullElse(newEventDto.getDescription(), event.getDescription()));
        event.setEventDate(Objects.requireNonNullElse(newEventDto.getEventDate(), event.getEventDate()));
        event.setAnnotation(Objects.requireNonNullElse(newEventDto.getAnnotation(), event.getAnnotation()));
    }

    private Event getEventByInitiatorIdAndEventId(Long userId, Long eventId) {
        return eventRepository.findByInitiatorIdAndId(userId, eventId)
                .orElseThrow(() -> new NotFoundException(String.format(EVENT_NOT_FOUND_MSG, eventId)));
    }

    private Event getEventByEventIdAndState(Long eventId) {
        return eventRepository.findEventByIdAndState(eventId, State.PUBLISHED)
                .orElseThrow(() -> new NotFoundException(String.format(EVENT_NOT_FOUND_MSG, eventId)));
    }
}

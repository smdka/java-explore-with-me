package ru.practicum.ewm.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.events.dto.State;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.exceptions.OperationException;
import ru.practicum.ewm.requests.dto.NewRequestUpdateDto;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.dto.RequestUpdateDto;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.requests.repository.RequestRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;

    private static final String REQUEST_ALREADY_EXISTS_MSG = "Пользователь с id=%d уже создал запрос на участие в событии с id=%d";
    private static final String REQUEST_SAME_USER_ID_EXCEPTION_MSG = "Нельзя создать запрос для собственного события";
    private static final String REQUEST_STATE_EXCEPTION_MSG = "Нельзя создать запрос для неопубликованного события";
    private static final String REQUEST_LIMIT_EXCEPTION_MSG = "Нельзя создать запрос из-за ограничения количества участников события";
    private static final String REQUEST_NOT_FOUND_EXCEPTION_MESSAGE = "Запрос с id=%s не найден";
    private static final String EVENT_NOT_FOUND_MESSAGE = "Событие с id=%s не найдено";

    @Override
    @Transactional
    public RequestDto add(Long userId, Long eventId) {
        return eventRepository.findById(eventId)
                .filter(event -> isEventAvailableForRequest(userId, event)
                        && !isRequestAlreadyExists(userId, eventId)
                        && isRequestsLimitNotExceeded(eventId, event))
                .map(event -> saveRequest(eventId, userId, event.getRequestModeration()))
                .orElseThrow(() -> new OperationException(String.format(EVENT_NOT_FOUND_MESSAGE, eventId)));
    }

    private boolean isEventAvailableForRequest(Long userId, Event event) {
        if (event.getInitiator().getId().equals(userId)) {
            throw new OperationException(REQUEST_SAME_USER_ID_EXCEPTION_MSG);
        }

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new OperationException(REQUEST_STATE_EXCEPTION_MSG);
        }

        return true;
    }

    private boolean isRequestAlreadyExists(Long userId, Long eventId) {
        requestRepository.findByRequesterIdAndEventId(userId, eventId)
                .orElseThrow(() -> new OperationException(String.format(REQUEST_ALREADY_EXISTS_MSG, userId, eventId)));

        return true;
    }

    private boolean isRequestsLimitNotExceeded(Long eventId, Event event) {
        if (requestRepository.countByEventId(eventId) >= event.getParticipantLimit()) {
            throw new OperationException(REQUEST_LIMIT_EXCEPTION_MSG);
        }

        return true;
    }

    private RequestDto saveRequest(Long eventId, Long userId, boolean isRequestedModeration) {
        Request request = RequestMapper.MAP.toModel(eventId, userId, isRequestedModeration ? State.PENDING : State.CONFIRMED);

        return RequestMapper.MAP.toDto(requestRepository.save(request));
    }

    @Override
    public Collection<RequestDto> getById(Long userId) {
        return RequestMapper.MAP.toDto(requestRepository.findAllByRequesterId(userId));
    }

    @Override
    public Collection<RequestDto> getByUserIdAndEventId(Long userId, Long eventId) {
        return RequestMapper.MAP.toDto(requestRepository.findAllByEventId(eventId));
    }

    @Override
    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        return requestRepository.findByRequesterIdAndId(userId, requestId)
                .map(this::setRequestStatusToCanceled)
                .orElseThrow(() -> new NotFoundException(String.format(REQUEST_NOT_FOUND_EXCEPTION_MESSAGE, requestId)));
    }

    private RequestDto setRequestStatusToCanceled(Request request) {
        request.setStatus(State.CANCELED);
        return RequestMapper.MAP.toDto(request);
    }

    @Override
    @Transactional
    public RequestUpdateDto update(Long userId, Long eventId, NewRequestUpdateDto newRequestUpdateDto) {
        return Optional.ofNullable(newRequestUpdateDto)
                .map(requestUpdateDto -> eventRepository.findById(eventId)
                        .filter(this::isEventParticipantLimitNotExceeded)
                        .map(event -> updateRequests(newRequestUpdateDto, new RequestUpdateDto(new ArrayList<>(), new ArrayList<>()), event))
                        .orElseThrow(() -> new NotFoundException(String.format(EVENT_NOT_FOUND_MESSAGE, eventId))))
                .orElseThrow(() -> new OperationException(REQUEST_LIMIT_EXCEPTION_MSG));
    }

    private boolean isEventParticipantLimitNotExceeded(Event event) {
        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new OperationException(REQUEST_LIMIT_EXCEPTION_MSG);
        }

        return true;
    }

    private RequestUpdateDto updateRequests(NewRequestUpdateDto newRequestUpdateDto,
                                            RequestUpdateDto requestUpdateDto,
                                            Event event) {

        if (event.getParticipantLimit().equals(0) || !event.getRequestModeration()) {
            return requestUpdateDto;
        }

        List<Long> requestIds = newRequestUpdateDto.getRequestIds();
        List<Request> requests = requestRepository.findAllById(requestIds);

        if (requests.isEmpty()) {
            throw new NotFoundException(String.format(REQUEST_NOT_FOUND_EXCEPTION_MESSAGE, requestIds));
        }

        requests.forEach(request -> {
            validateRequestStatus(newRequestUpdateDto, request);
            updateRequestStatusAndSave(newRequestUpdateDto, requestUpdateDto, event, request);
        });

        return requestUpdateDto;
    }

    private void validateRequestStatus(NewRequestUpdateDto newRequestUpdateDto, Request request) {
        if (request.getStatus().equals(State.CONFIRMED) && newRequestUpdateDto.getStatus().equals(State.REJECTED)) {
            throw new OperationException(REQUEST_LIMIT_EXCEPTION_MSG);
        }
    }

    private void updateRequestStatusAndSave(NewRequestUpdateDto newRequestUpdateDto,
                                            RequestUpdateDto requestUpdateDto,
                                            Event event, Request request) {

        if (event.getConfirmedRequests() >= event.getParticipantLimit() || newRequestUpdateDto.getStatus().equals(State.REJECTED)) {
            setRequestStatusAndSave(requestUpdateDto.getRejectedRequests(), request, State.REJECTED);
        } else {
            setRequestStatusAndSave(requestUpdateDto.getConfirmedRequests(), request, newRequestUpdateDto.getStatus());
            increaseEventConfirmedRequests(event);
        }
    }

    private void setRequestStatusAndSave(List<RequestDto> updatedRequests, Request request, State status) {
        request.setStatus(status);

        requestRepository.save(request);

        updatedRequests.add(RequestMapper.MAP.toDto(request));
    }

    private void increaseEventConfirmedRequests(Event event) {
        eventRepository.increaseConfirmedRequests(event.getId(), 1);
    }
}

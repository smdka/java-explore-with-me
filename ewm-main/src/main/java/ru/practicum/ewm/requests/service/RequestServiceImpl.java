package ru.practicum.ewm.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.events.dto.State;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.exception.NotFoundException;
import ru.practicum.ewm.exception.OperationException;
import ru.practicum.ewm.requests.dto.NewRequestUpdateDto;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.dto.RequestUpdateDto;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.requests.repository.RequestRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private static final String REQUEST_ALREADY_EXISTS_MSG = "Пользователь с id=%d уже создал запрос на участие в событии с id=%d";
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;

    private static final String REQUEST_SAME_USER_ID_EXCEPTION_MESSAGE =
            "Cannot create request for own event.";
    private static final String REQUEST_STATE_EXCEPTION_MESSAGE =
            "Cannot create request for unpublished event.";
    private static final String REQUEST_LIMIT_EXCEPTION_MESSAGE =
            "Cannot create request because of participant limitation.";
    private static final String REQUEST_NOT_FOUND_EXCEPTION_MESSAGE =
            "Request with id=%s was not found";
    private static final String EVENT_NOT_FOUND_MESSAGE = "Event with id=%s was not found";

    @Override
    @Transactional
    public RequestDto create(Long userId, Long eventId) {
        Event event = findEventById(eventId);

        validateEventForRequest(userId, event);

        List<Request> requests = new ArrayList<>(requestRepository.findAllByEventId(eventId));

        checkIfRequestAlreadyExists(userId, eventId, requests);
        checkParticipantLimit(event, requests);

        return saveRequest(eventId, userId, event.getRequestModeration());
    }

    private void checkIfRequestAlreadyExists(Long userId, Long eventId, List<Request> requests) {
        boolean requestByUserExists = requests.stream()
                .map(Request::getRequesterId)
                .anyMatch(requesterId -> Objects.equals(requesterId, userId));

        if (requestByUserExists) {
            throw new OperationException(String.format(REQUEST_ALREADY_EXISTS_MSG, userId, eventId));
        }
    }

    @Override
    public Collection<RequestDto> getById(Long userId) {
        return RequestMapper.toDto(requestRepository.findAllByRequesterId(userId));
    }

    @Override
    public Collection<RequestDto> getByUserIdAndEventId(Long userId, Long eventId) {
        return RequestMapper.toDto(requestRepository.findAllByEventId(eventId));
    }

    @Override
    @Transactional
    public RequestDto cancelRequest(Long userId, Long requestId) {
        Request request = getRequestByRequesterIdAndId(userId, requestId);

        request.setStatus(State.CANCELED);

        return RequestMapper.toDto(request);
    }

    private Request getRequestByRequesterIdAndId(Long userId, Long requestId) {
        Request request = requestRepository.findByRequesterIdAndId(userId, requestId);

        if (request == null) {
            throw new NotFoundException(String.format(REQUEST_NOT_FOUND_EXCEPTION_MESSAGE, requestId));
        }

        return request;
    }

    @Override
    @Transactional
    public RequestUpdateDto update(Long userId, Long eventId, NewRequestUpdateDto newRequestUpdateDto) {
        validateNewRequestUpdateDto(newRequestUpdateDto);

        RequestUpdateDto requestUpdateDto = new RequestUpdateDto(new ArrayList<>(), new ArrayList<>());
        Event event = findEventById(eventId);

        checkEventParticipantLimit(event);

        updateRequests(newRequestUpdateDto, requestUpdateDto, event);

        return requestUpdateDto;
    }

    private void validateEventForRequest(Long userId, Event event) {
        validateEventInitiator(userId, event);
        validateEventState(event);
    }

    private void validateEventInitiator(Long userId, Event event) {
        if (event.getInitiator().getId().equals(userId)) {
            throw new OperationException(REQUEST_SAME_USER_ID_EXCEPTION_MESSAGE);
        }
    }

    private void validateEventState(Event event) {
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new OperationException(REQUEST_STATE_EXCEPTION_MESSAGE);
        }
    }

    private void checkParticipantLimit(Event event, List<Request> requests) {
        if (requests.size() >= event.getParticipantLimit()) {
            throw new OperationException(REQUEST_LIMIT_EXCEPTION_MESSAGE);
        }
    }

    private RequestDto saveRequest(Long eventId, Long userId, Boolean isRequestedModeration) {
        Request request = RequestMapper.toModel(eventId, userId,
                isRequestedModeration ? State.PENDING : State.CONFIRMED);

        return RequestMapper.toDto(requestRepository.save(request));
    }

    private void validateNewRequestUpdateDto(NewRequestUpdateDto newRequestUpdateDto) {
        if (newRequestUpdateDto == null) {
            throw new OperationException(REQUEST_LIMIT_EXCEPTION_MESSAGE);
        }
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new OperationException(String.format(EVENT_NOT_FOUND_MESSAGE, eventId)));
    }

    private void checkEventParticipantLimit(Event event) {
        if (event.getConfirmedRequests() >= event.getParticipantLimit()) {
            throw new OperationException(REQUEST_LIMIT_EXCEPTION_MESSAGE);
        }
    }

    private void updateRequests(NewRequestUpdateDto newRequestUpdateDto,
                                RequestUpdateDto requestUpdateDto, Event event) {
        List<Long> requestIds = newRequestUpdateDto.getRequestIds();

        for (Long id : requestIds) {
            if (event.getParticipantLimit().equals(0) || !event.getRequestModeration()) {
                break;
            }

            Request request = getRequestById(id);

            validateRequestStatus(newRequestUpdateDto, request);
            updateRequestStatusAndSave(newRequestUpdateDto, requestUpdateDto, event, request);
        }
    }

    private Request getRequestById(Long id) {
        return requestRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format(REQUEST_NOT_FOUND_EXCEPTION_MESSAGE, id)));
    }

    private void validateRequestStatus(NewRequestUpdateDto newRequestUpdateDto, Request request) {
        if (request.getStatus().equals(State.CONFIRMED) &&
                newRequestUpdateDto.getStatus().equals(State.REJECTED)) {
            throw new OperationException(REQUEST_LIMIT_EXCEPTION_MESSAGE);
        }
    }

    private void updateRequestStatusAndSave(NewRequestUpdateDto newRequestUpdateDto,
                                            RequestUpdateDto requestUpdateDto,
                                            Event event, Request request) {
        if (event.getConfirmedRequests() >= event.getParticipantLimit()
                || newRequestUpdateDto.getStatus().equals(State.REJECTED)) {
            setRequestStatusAndSave(requestUpdateDto.getRejectedRequests(), request, State.REJECTED);
        } else {
            setRequestStatusAndSave(requestUpdateDto.getConfirmedRequests(),
                    request, newRequestUpdateDto.getStatus());

            incrementEventConfirmedRequests(event);
        }
    }

    private void setRequestStatusAndSave(List<RequestDto> updatedRequests,
                                         Request request, State status) {
        request.setStatus(status);

        requestRepository.save(request);

        updatedRequests.add(RequestMapper.toDto(request));
    }

    private void incrementEventConfirmedRequests(Event event) {
        event.setConfirmedRequests(event.getConfirmedRequests() + 1);

        eventRepository.save(event);
    }
}

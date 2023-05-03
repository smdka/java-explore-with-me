package ru.practicum.ewm.requests.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
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

import static org.springframework.data.domain.ExampleMatcher.GenericPropertyMatchers.exact;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;

    private static final String REQUEST_ALREADY_EXISTS_MSG = "Пользователь с id=%d уже создал запрос на участие в событии с id=%d";

    private static final String REQUEST_SAME_USER_ID_EXCEPTION_MSG =
            "Нельзя создать запрос для собственного события";
    private static final String REQUEST_STATE_EXCEPTION_MESSAGE =
            "Нельзя создать запрос для неопубликованного события";
    private static final String REQUEST_LIMIT_EXCEPTION_MESSAGE =
            "Нельзя создать запрос из-за ограничения количества участников события";
    private static final String REQUEST_NOT_FOUND_EXCEPTION_MESSAGE =
            "Запрос с id=%s не найден";
    private static final String EVENT_NOT_FOUND_MESSAGE = "Событие с id=%s не найдено";

    @Override
    @Transactional
    public RequestDto add(Long userId, Long eventId) {
        Event event = findEventById(eventId);

        validateEventForRequest(userId, event);

        checkIfRequestAlreadyExists(userId, eventId);

        checkParticipantLimit(eventId, event);

        return saveRequest(eventId, userId, event.getRequestModeration());
    }

    private void validateEventForRequest(Long userId, Event event) {
        validateEventInitiator(userId, event);
        validateEventState(event);
    }

    private void validateEventInitiator(Long userId, Event event) {
        if (event.getInitiator().getId().equals(userId)) {
            throw new OperationException(REQUEST_SAME_USER_ID_EXCEPTION_MSG);
        }
    }

    private void validateEventState(Event event) {
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new OperationException(REQUEST_STATE_EXCEPTION_MESSAGE);
        }
    }
    private void checkParticipantLimit(Long eventId, Event event) {
        if (requestRepository.countByEventId(eventId) >= event.getParticipantLimit()) {
            throw new OperationException(REQUEST_LIMIT_EXCEPTION_MESSAGE);
        }
    }

    private void checkIfRequestAlreadyExists(Long userId, Long eventId) {
        ExampleMatcher requesterMatcher = ExampleMatcher.matchingAny()
                .withIgnorePaths("id")
                .withMatcher("requester_id", exact());

        Request request = new Request();
        request.setRequesterId(userId);

        Example<Request> example = Example.of(request, requesterMatcher);

        if (requestRepository.exists(example)) {
            throw new OperationException(String.format(REQUEST_ALREADY_EXISTS_MSG, userId, eventId));
        }
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
        Request request = getRequestByRequesterIdAndId(userId, requestId);

        request.setStatus(State.CANCELED);

        return RequestMapper.MAP.toDto(request);
    }

    private Request getRequestByRequesterIdAndId(Long userId, Long requestId) {
        return requestRepository.findByRequesterIdAndId(userId, requestId)
                .orElseThrow(() -> new NotFoundException(String.format(REQUEST_NOT_FOUND_EXCEPTION_MESSAGE, requestId)));
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


    private RequestDto saveRequest(Long eventId, Long userId, Boolean isRequestedModeration) {
        Request request = RequestMapper.MAP.toModel(eventId, userId,
                isRequestedModeration ? State.PENDING : State.CONFIRMED);

        return RequestMapper.MAP.toDto(requestRepository.save(request));
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

        updatedRequests.add(RequestMapper.MAP.toDto(request));
    }

    private void incrementEventConfirmedRequests(Event event) {
        event.setConfirmedRequests(event.getConfirmedRequests() + 1);

        eventRepository.save(event);
    }
}

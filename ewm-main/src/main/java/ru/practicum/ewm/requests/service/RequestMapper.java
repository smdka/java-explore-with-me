package ru.practicum.ewm.requests.service;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.events.dto.State;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.model.Request;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RequestMapper {
    public static RequestDto toDto(Request request) {
        return new RequestDto(
                request.getId(),
                request.getEventId(),
                request.getRequesterId(),
                request.getStatus(),
                request.getCreated());
    }

    public static List<RequestDto> toDto(Collection<Request> requests) {
        return requests
                .stream()
                .map(RequestMapper::toDto)
                .collect(Collectors.toList());
    }

    public static Request toModel(Long eventId, Long requesterId, State status) {
        Request request = new Request();

        request.setRequesterId(requesterId);
        request.setEventId(eventId);
        request.setStatus(status);

        return request;
    }
}
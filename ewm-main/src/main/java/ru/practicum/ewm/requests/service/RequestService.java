package ru.practicum.ewm.requests.service;

import ru.practicum.ewm.requests.dto.NewRequestUpdateDto;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.dto.RequestUpdateDto;

import java.util.Collection;

public interface RequestService {
    RequestDto add(Long userId, Long eventId);

    Collection<RequestDto> getById(Long userId);

    Collection<RequestDto> getByUserIdAndEventId(Long userId, Long eventId);

    RequestDto cancelRequest(Long userId, Long requestId);

    RequestUpdateDto update(Long userId, Long eventId, NewRequestUpdateDto newRequestUpdateDto);
}

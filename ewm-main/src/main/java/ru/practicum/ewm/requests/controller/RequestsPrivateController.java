package ru.practicum.ewm.requests.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.requests.dto.NewRequestUpdateDto;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.dto.RequestUpdateDto;
import ru.practicum.ewm.requests.service.RequestService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("users")
public class RequestsPrivateController {
    private final RequestService requestService;

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public RequestDto create(@PathVariable Long userId,
                             @RequestParam Long eventId) {
        log.info("POST /users/{}/requests?eventId={}", userId, eventId);
        return requestService.create(userId, eventId);
    }

    @GetMapping("/{userId}/requests")
    public Collection<RequestDto> getRequestsById(@PathVariable Long userId) {
        log.info("GET /users/{}/requests", userId);
        return requestService.getById(userId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public Collection<RequestDto> getRequestsByUserIdAndEventId(@PathVariable Long userId,
                                                                @PathVariable Long eventId) {
        log.info("GET /users/{}/events/{}/requests", userId, eventId);
        return requestService.getByUserIdAndEventId(userId, eventId);
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    public RequestDto cancelRequest(@PathVariable Long userId,
                                    @PathVariable Long requestId) {
        log.info("PATCH /users/{}/requests/{}/cancel", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public RequestUpdateDto updateRequestStatus(@PathVariable Long userId,
                                                @PathVariable Long eventId,
                                                @RequestBody(required = false) NewRequestUpdateDto newRequestUpdateDto) {
        log.info("PATCH /users/{}/events/{}/requests", userId, eventId);
        log.info("Request body: {}", newRequestUpdateDto);
        return requestService.update(userId, eventId, newRequestUpdateDto);
    }
}
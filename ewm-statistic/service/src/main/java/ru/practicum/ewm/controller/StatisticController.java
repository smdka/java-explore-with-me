package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.service.EndpointHitService;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatisticController {
    private final EndpointHitService service;

    @PostMapping("hit")
    @ResponseStatus(HttpStatus.CREATED)
    public void post(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        log.info("POST /hit");
        log.info("Request body: {}", endpointHitDto);
        service.add(endpointHitDto);
    }

    @GetMapping("stats")
    public Collection<ViewStatsDto> get(@RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
                                        @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
                                        @RequestParam(required = false) List<String> uris,
                                        boolean unique) {
        log.info("GET /stats?start={}&end={}&uris={}&unique={}", start, end, uris, unique);

        Collection<ViewStatsDto> response = service.get(start, end, uris, unique);

        log.info("Response: {}", response);
        return response;
    }
}

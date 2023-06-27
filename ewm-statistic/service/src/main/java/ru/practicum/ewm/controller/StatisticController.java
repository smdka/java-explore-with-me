package ru.practicum.ewm.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
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

    @PostMapping( "${stats-server.hit.endpoint}")
    @ResponseStatus(HttpStatus.CREATED)
    public void post(@Valid @RequestBody EndpointHitDto endpointHitDto) {
        log.info("POST /hit");
        log.info("Request body: {}", endpointHitDto);
        service.add(endpointHitDto);
    }

    @GetMapping("${stats-server.stats.endpoint}")
    public Collection<ViewStatsDto> get(@RequestParam LocalDateTime start,
                                        @RequestParam LocalDateTime end,
                                        @RequestParam(required = false) List<String> uris,
                                        boolean unique) {
        log.info("GET /stats?start={}&end={}&uris={}&unique={}", start, end, uris, unique);

        Collection<ViewStatsDto> response = service.get(start, end, uris, unique);

        log.info("Response: {}", response);
        return response;
    }
}

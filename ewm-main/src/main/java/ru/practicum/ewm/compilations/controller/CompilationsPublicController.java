package ru.practicum.ewm.compilations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.service.CompilationsService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("compilations")
public class CompilationsPublicController {
    private final CompilationsService service;

    @GetMapping
    public Collection<CompilationDto> getAll(@RequestParam(required = false) Boolean pinned,
                                             @RequestParam(defaultValue = "0") Integer from,
                                             @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /compilations?pinned={}&from={}&size={}", pinned, from, size);
        return service.getAll(pinned, from, size);
    }

    @GetMapping("/{compId}")
    public CompilationDto getById(@PathVariable Long compId) {
        log.info("GET /compilations/{}", compId);
        return service.getById(compId);
    }
}

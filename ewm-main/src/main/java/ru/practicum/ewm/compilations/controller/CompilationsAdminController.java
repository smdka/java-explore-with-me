package ru.practicum.ewm.compilations.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.compilations.service.CompilationsService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("admin/compilations")
public class CompilationsAdminController {
    private final CompilationsService service;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CompilationDto create(@RequestBody @Valid NewCompilationDto newCompilationDto) {
        log.info("POST /admin/compilations");
        log.info("Request body: {}", newCompilationDto);

        CompilationDto response = service.add(newCompilationDto);

        log.info("Response: {}", response);
        return response;
    }

    @PatchMapping("/{compId}")
    public CompilationDto update(@PathVariable Long compId,
                                 @RequestBody NewCompilationDto newCompilationDto) {
        log.info("PATCH /admin/compilations/{}", compId);
        log.info("Request body: {}", newCompilationDto);
        return service.update(compId, newCompilationDto);
    }

    @DeleteMapping("/{compId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Object> delete(@PathVariable Long compId) {
        log.info("DELETE /admin/compilations/{}", compId);
        service.delete(compId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}

package ru.practicum.ewm.compilations.service;

import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.NewCompilationDto;

import java.util.Collection;

public interface CompilationsService {
    Collection<CompilationDto> getAll(Boolean pinned, Integer from, Integer size);

    CompilationDto getById(Long compId);

    CompilationDto add(NewCompilationDto newCompilationDto);

    CompilationDto update(Long compId, NewCompilationDto newCompilationDto);

    void delete(Long compId);
}

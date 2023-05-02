package ru.practicum.ewm.compilations.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.compilations.model.Compilation;
import ru.practicum.ewm.compilations.repository.CompilationRepository;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.exceptions.NotFoundException;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CompilationsServiceImpl implements CompilationsService {
    private final CompilationRepository compRepository;

    private final EventRepository eventRepository;
    private static final String COMP_NOT_FOUND_MSG = "Compilation with id=%s was not found";

    @Override
    public Collection<CompilationDto> getAll(Boolean pinned, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);

        return CompilationMapper.toCompilationDto(compRepository.getAll(pinned, pageable));
    }

    @Override
    public CompilationDto getById(Long compId) {
        Compilation compilation = compRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format(COMP_NOT_FOUND_MSG, compId)));

        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto add(NewCompilationDto newCompilationDto) {
        Compilation compilation = compRepository.save(CompilationMapper.toCompilation(newCompilationDto));

        List<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());

        compilation.getEvents().addAll(events);

        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public CompilationDto update(Long compId, NewCompilationDto newCompilationDto) {
        Compilation compilation = compRepository.findById(compId)
                .orElseThrow(() -> new NotFoundException(String.format(COMP_NOT_FOUND_MSG, compId)));
        Collection<Event> events = eventRepository.findAllById(newCompilationDto.getEvents());

        compilation.setTitle(Objects.requireNonNullElse(newCompilationDto.getTitle(),
                compilation.getTitle()));
        compilation.setPinned(Objects.requireNonNullElse(newCompilationDto.getPinned(),
                compilation.getPinned()));
        compilation.setEvents(new HashSet<>(events));

        return CompilationMapper.toCompilationDto(compilation);
    }

    @Override
    @Transactional
    public void delete(Long compId) {
        if (compRepository.existsById(compId)) {
            compRepository.deleteById(compId);
        } else {
            throw new NotFoundException(String.format(COMP_NOT_FOUND_MSG, compId));
        }
    }
}

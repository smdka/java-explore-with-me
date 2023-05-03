package ru.practicum.ewm.compilations.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.compilations.dto.CompilationDto;
import ru.practicum.ewm.compilations.dto.NewCompilationDto;
import ru.practicum.ewm.compilations.model.Compilation;

import java.util.List;

@Mapper
public interface CompilationMapper {
    CompilationMapper MAP = Mappers.getMapper(CompilationMapper.class);

    List<CompilationDto> toDto(List<Compilation> compilations);

    CompilationDto toDto(Compilation compilation);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", expression = "java(new java.util.HashSet<>())")
    Compilation toModel(NewCompilationDto newCompilationDto);
}
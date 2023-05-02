package ru.practicum.ewm.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.dto.EndpointHitDto;


@Mapper
public interface EndpointHitMapper {
    EndpointHitMapper MAP = Mappers.getMapper(EndpointHitMapper.class);

    @Mapping(target = "id", ignore = true)
    EndpointHit toModel(EndpointHitDto endpointHitDto);
}

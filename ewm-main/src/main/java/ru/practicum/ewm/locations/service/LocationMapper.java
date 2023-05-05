package ru.practicum.ewm.locations.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.locations.dto.LocationDto;
import ru.practicum.ewm.locations.dto.NewLocationDto;
import ru.practicum.ewm.locations.model.Location;

@Mapper
public interface LocationMapper {
    LocationMapper MAP = Mappers.getMapper(LocationMapper.class);

    @Mapping(target = "id", ignore = true)
    Location toModel(NewLocationDto newLocationDto);

    LocationDto toDto(Location location);
}
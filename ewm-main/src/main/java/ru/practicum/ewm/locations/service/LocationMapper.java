package ru.practicum.ewm.locations.service;

import ru.practicum.ewm.locations.dto.LocationDto;
import ru.practicum.ewm.locations.dto.NewLocationDto;
import ru.practicum.ewm.locations.model.Location;

public class LocationMapper {

    private LocationMapper() {
    }

    public static Location toModel(NewLocationDto newLocationDto) {
        Location location = new Location();

        location.setLat(newLocationDto.getLat());
        location.setLon(newLocationDto.getLon());

        return location;
    }

    public static LocationDto toDto(Location location) {
        return new LocationDto(location.getId(), location.getLat(), location.getLon());
    }
}
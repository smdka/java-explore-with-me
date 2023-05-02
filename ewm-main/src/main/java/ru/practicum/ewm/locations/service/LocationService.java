package ru.practicum.ewm.locations.service;

import ru.practicum.ewm.locations.dto.LocationDto;
import ru.practicum.ewm.locations.dto.NewLocationDto;

public interface LocationService {

    LocationDto create(NewLocationDto newLocationDto);
}
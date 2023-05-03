package ru.practicum.ewm.locations.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.locations.dto.LocationDto;
import ru.practicum.ewm.locations.dto.NewLocationDto;
import ru.practicum.ewm.locations.model.Location;
import ru.practicum.ewm.locations.repository.LocationRepository;

@Service
@RequiredArgsConstructor
public class LocationServiceImpl implements LocationService {
    private final LocationRepository locationRepository;

    @Override
    @Transactional
    public LocationDto add(NewLocationDto newLocationDto) {
        Location location = LocationMapper.MAP.toModel(newLocationDto);

        return LocationMapper.MAP.toDto(locationRepository.save(location));
    }
}
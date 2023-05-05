package ru.practicum.ewm.locations.dto;

import lombok.Value;

@Value
public class LocationDto {
    Long id;
    Float lat;
    Float lon;
}
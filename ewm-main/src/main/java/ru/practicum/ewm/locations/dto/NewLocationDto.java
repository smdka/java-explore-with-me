package ru.practicum.ewm.locations.dto;

import lombok.Value;

import javax.validation.constraints.NotNull;

@Value
public class NewLocationDto {
    @NotNull
    Float lat;

    @NotNull
    Float lon;
}
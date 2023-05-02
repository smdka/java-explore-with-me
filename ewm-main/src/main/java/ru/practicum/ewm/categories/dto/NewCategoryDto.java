package ru.practicum.ewm.categories.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
public class NewCategoryDto {
    @NotBlank
    String name;
}
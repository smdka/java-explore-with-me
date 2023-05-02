package ru.practicum.ewm.categories.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
@NoArgsConstructor(access = AccessLevel.PRIVATE, force = true)
public class NewCategoryDto {
    @NotBlank
    String name;
}
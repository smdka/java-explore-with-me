package ru.practicum.ewm.categories.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.hateoas.RepresentationModel;

@EqualsAndHashCode(callSuper = true)
@Value
public class CategoryDto extends RepresentationModel<CategoryDto> {
    Long id;
    String name;
}
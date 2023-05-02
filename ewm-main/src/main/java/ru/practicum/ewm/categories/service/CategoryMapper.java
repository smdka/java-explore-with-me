package ru.practicum.ewm.categories.service;

import lombok.experimental.UtilityClass;
import org.springframework.data.domain.Page;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.NewCategoryDto;
import ru.practicum.ewm.categories.model.Category;

import java.util.List;

import static java.util.stream.Collectors.toList;

@UtilityClass
public class CategoryMapper {
    public static Category toModel(NewCategoryDto newCategoryDto) {
        Category category = new Category();

        category.setName(newCategoryDto.getName());

        return category;
    }

    public static CategoryDto toDto(Category category) {
        return new CategoryDto(category.getId(), category.getName());
    }

    public static List<CategoryDto> toDto(Page<Category> categories) {
        return categories
                .stream()
                .map(CategoryMapper::toDto)
                .collect(toList());
    }
}
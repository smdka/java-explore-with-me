package ru.practicum.ewm.categories.service;

import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.NewCategoryDto;

import java.util.Collection;

public interface CategoryService {
    Collection<CategoryDto> getAll(Integer from, Integer size);

    CategoryDto getById(Long categoryId);

    CategoryDto add(NewCategoryDto newCategoryDto);

    void delete(Long categoryId);

    CategoryDto update(Long categoryId, NewCategoryDto newCategoryDto);
}

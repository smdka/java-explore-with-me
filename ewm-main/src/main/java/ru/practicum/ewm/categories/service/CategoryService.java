package ru.practicum.ewm.categories.service;

import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.NewCategoryDto;

import java.util.Collection;

public interface CategoryService {
    Collection<CategoryDto> getAll(Integer from, Integer size);

    CategoryDto getById(Long categoryId, Integer from, Integer size);

    CategoryDto add(NewCategoryDto newCategoryDto);

    void deleteById(Long categoryId);

    CategoryDto updateCategoryById(Long categoryId, NewCategoryDto newCategoryDto);
}

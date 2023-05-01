package ru.practicum.ewm.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.service.CategoryService;

import java.util.Collection;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("categories")
public class CategoriesPublicController {
    private final CategoryService categoryService;

    @GetMapping
    public Collection<CategoryDto> getCategories(@RequestParam(defaultValue = "0") Integer from,
                                                 @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /categories?from={}&size={}", from, size);

        Collection<CategoryDto> response = categoryService.getAll(from, size);

        log.info("Response: {}", response);
        return response;
    }

    @GetMapping("/{categoryId}")
    public CategoryDto getCategoryById(@PathVariable Long categoryId,
                                       @RequestParam(value = "from", defaultValue = "0") Integer from,
                                       @RequestParam(value = "size", defaultValue = "10") Integer size) {
        log.info("GET /categories/{}?from={}&size={}", categoryId, from, size);

        CategoryDto response = categoryService.getById(categoryId, from, size);

        log.info("Response: {}", response);
        return response;
    }
}
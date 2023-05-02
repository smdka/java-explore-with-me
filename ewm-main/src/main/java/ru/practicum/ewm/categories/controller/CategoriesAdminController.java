package ru.practicum.ewm.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.NewCategoryDto;
import ru.practicum.ewm.categories.service.CategoryService;

import javax.validation.Valid;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("admin/categories")
public class CategoriesAdminController {
    private final CategoryService categoryService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CategoryDto create(@RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("POST /admin/categories");
        log.info("Request body: {}", newCategoryDto);

        CategoryDto response = categoryService.add(newCategoryDto);

        log.info("Response: {}", response);
        return response;
    }

    @DeleteMapping("/{categoryId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCategoryById(@PathVariable Long categoryId) {
        log.info("DELETE /admin/categories/{}", categoryId);
        categoryService.deleteById(categoryId);
    }

    @PatchMapping("/{categoryId}")
    public CategoryDto updateCategoryById(@PathVariable Long categoryId,
                                          @RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("PATCH /admin/categories/{}", categoryId);
        log.info("Request body: {}", newCategoryDto);

        CategoryDto response = categoryService.updateById(categoryId, newCategoryDto);

        log.info("Response: {}", response);
        return response;
    }
}
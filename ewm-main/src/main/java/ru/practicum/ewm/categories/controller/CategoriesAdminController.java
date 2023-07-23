package ru.practicum.ewm.categories.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<Object> deleteById(@PathVariable Long categoryId) {
        log.info("DELETE /admin/categories/{}", categoryId);
        categoryService.delete(categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/{categoryId}")
    public CategoryDto updateById(@PathVariable Long categoryId,
                                  @RequestBody @Valid NewCategoryDto newCategoryDto) {
        log.info("PATCH /admin/categories/{}", categoryId);
        log.info("Request body: {}", newCategoryDto);

        CategoryDto response = categoryService.update(categoryId, newCategoryDto);

        log.info("Response: {}", response);
        return response;
    }
}
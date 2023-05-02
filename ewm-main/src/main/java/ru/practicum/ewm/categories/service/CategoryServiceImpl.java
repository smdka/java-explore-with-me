package ru.practicum.ewm.categories.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.dto.NewCategoryDto;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.categories.repository.CategoryRepository;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.exceptions.OperationException;

import java.util.Collection;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    private final EventRepository eventRepository;

    private static final String CATEGORY_NOT_FOUND_MESSAGE = "Category with id=%s was not found";

    @Override
    public Collection<CategoryDto> getAll(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);

        return CategoryMapper.toDto(categoryRepository.findAll(pageable));
    }

    @Override
    public CategoryDto getById(Long categoryId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);
        List<Category> categories = categoryRepository.findById(categoryId, pageable);

        if (categories.isEmpty()) {
            throw new NotFoundException(String.format(CATEGORY_NOT_FOUND_MESSAGE, categoryId));
        }

        return CategoryMapper.toDto(categories.get(0));
    }

    @Override
    @Transactional
    public CategoryDto add(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.toModel(newCategoryDto);

        return CategoryMapper.toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void deleteById(Long categoryId) {
        if (!eventRepository.findAllByCategoryId(categoryId).isEmpty()) {
            throw new OperationException("The category is not empty");
        }

        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException(String.format(CATEGORY_NOT_FOUND_MESSAGE, categoryId));
        }

        categoryRepository.deleteById(categoryId);
    }

    @Override
    @Transactional
    public CategoryDto updateCategoryById(Long categoryId, NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format(CATEGORY_NOT_FOUND_MESSAGE, categoryId)));

        category.setName(newCategoryDto.getName());

        return CategoryMapper.toDto(category);
    }
}

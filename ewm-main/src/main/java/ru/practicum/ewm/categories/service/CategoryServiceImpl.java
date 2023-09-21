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
import java.util.Optional;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private static final String CATEGORY_NOT_FOUND_MSG = "Категория с id=%s не найдена";
    private static final String CATEGORY_HAS_RELATED_EVENTS_MSG = "С категорией с id=%d связаны события";

    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;


    @Override
    public Collection<CategoryDto> getAll(Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);

        return CategoryMapper.MAP.toDto(categoryRepository.findAll(pageable));
    }

    @Override
    public CategoryDto getById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .map(CategoryMapper.MAP::toDto)
                .orElseThrow(() -> new NotFoundException(String.format(CATEGORY_NOT_FOUND_MSG, categoryId)));
    }

    @Override
    @Transactional
    public CategoryDto add(NewCategoryDto newCategoryDto) {
        Category category = CategoryMapper.MAP.toModel(newCategoryDto);

        return CategoryMapper.MAP.toDto(categoryRepository.save(category));
    }

    @Override
    @Transactional
    public void delete(Long categoryId) {
        Optional.ofNullable(eventRepository.findByCategoryId(categoryId))
                .ifPresentOrElse(event -> {
                    throw new OperationException(String.format(CATEGORY_HAS_RELATED_EVENTS_MSG, categoryId));
                }, () -> categoryRepository.findById(categoryId)
                        .ifPresentOrElse(category -> categoryRepository.deleteById(categoryId),
                                () -> {
                                    throw new NotFoundException(String.format(CATEGORY_NOT_FOUND_MSG, categoryId));
                                }));
    }

    @Override
    @Transactional
    public CategoryDto update(Long categoryId, NewCategoryDto newCategoryDto) {
        return categoryRepository.findById(categoryId)
                .map(category -> updateCategory(newCategoryDto, category))
                .orElseThrow(() -> new NotFoundException(String.format(CATEGORY_NOT_FOUND_MSG, categoryId)));
    }

    private CategoryDto updateCategory(NewCategoryDto newCategoryDto, Category category) {
        category.setName(newCategoryDto.getName());
        return CategoryMapper.MAP.toDto(categoryRepository.save(category));
    }
}

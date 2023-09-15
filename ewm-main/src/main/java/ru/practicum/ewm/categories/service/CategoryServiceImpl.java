package ru.practicum.ewm.categories.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
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
    public CategoryDto getById(Long categoryId, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);

        return categoryRepository.findAllById(categoryId, pageable)
                .stream()
                .findFirst()
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
        if (eventRepository.findFirstByCategoryId(categoryId) != null) {
            throw new OperationException(String.format(CATEGORY_HAS_RELATED_EVENTS_MSG, categoryId));
        }

        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException(String.format(CATEGORY_NOT_FOUND_MSG, categoryId));
        }

        categoryRepository.deleteById(categoryId);
    }

    @Override
    @Transactional
    public CategoryDto update(Long categoryId, NewCategoryDto newCategoryDto) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format(CATEGORY_NOT_FOUND_MSG, categoryId)));

        category.setName(newCategoryDto.getName());

        return CategoryMapper.MAP.toDto(categoryRepository.save(category));
    }
}

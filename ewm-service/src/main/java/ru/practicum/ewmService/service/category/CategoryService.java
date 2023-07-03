package ru.practicum.ewmService.service.category;

import ru.practicum.ewmService.dto.category.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    NewCategoryDto createCategory(NewCategoryDto newCategoryDto);

    void deleteCategoryById(Long id);

    NewCategoryDto updateCategory(Long id, NewCategoryDto newCategoryDto);

    List<NewCategoryDto> findAllCategories(Integer from, Integer size);

    NewCategoryDto findCategoryById(Long id);
}
package ru.practicum.ewmService.mapper.category;

import org.mapstruct.Mapper;
import ru.practicum.ewmService.dto.category.NewCategoryDto;
import ru.practicum.ewmService.model.category.Category;

@Mapper(componentModel = "spring")
public interface CategoryMapper {

    NewCategoryDto toCategoryDto(Category category);

    Category toCategory(NewCategoryDto newCategoryDto);
}
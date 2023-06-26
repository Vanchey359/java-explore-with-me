package ru.practicum.ewmService.service.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmService.dto.category.NewCategoryDto;
import ru.practicum.ewmService.exception.ConflictException;
import ru.practicum.ewmService.exception.NotFoundException;
import ru.practicum.ewmService.mapper.category.CategoryMapper;
import ru.practicum.ewmService.model.category.Category;
import ru.practicum.ewmService.model.event.Event;
import ru.practicum.ewmService.repository.category.CategoryRepository;
import ru.practicum.ewmService.repository.event.EventRepository;

import java.util.List;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventsRepository;
    private final CategoryMapper categoryMapper;

    @Transactional
    public NewCategoryDto createCategory(NewCategoryDto newCategoryDto) {
        Category category = categoryMapper.toCategory(newCategoryDto);
        log.info("Created new category=" + newCategoryDto.toString());

        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Transactional
    public void deleteCategoryById(Long categoryId) {
        Category category = checkCategory(categoryId);
        List<Event> events = eventsRepository.findByCategory(category);
        if (!events.isEmpty()) {
            throw new ConflictException("Cannot delete the category with id=" + categoryId +
                    " because of existing event in this category");
        }
        categoryRepository.deleteById(categoryId);
        log.info("Deleted category with id=" + categoryId);
    }

    @Transactional
    public NewCategoryDto updateCategory(Long categoryId, NewCategoryDto newCategoryDto) {
        Category category = checkCategory(categoryId);
        ofNullable(newCategoryDto.getName()).ifPresent(category::setName);
        Category newCategory = categoryRepository.save(category);
        log.info("Updated category with id=" + categoryId);

        return categoryMapper.toCategoryDto(newCategory);
    }

    public List<NewCategoryDto> findAllCategories(Integer from, Integer size) {
        PageRequest page = PageRequest.of(from, size);
        List<NewCategoryDto> newCategoryDtoList = categoryRepository.findAll(page).stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
        log.info("Found all categories with size=" + newCategoryDtoList.size());

        return newCategoryDtoList;
    }

    public NewCategoryDto findCategoryById(Long categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " not found"));
        return categoryMapper.toCategoryDto(category);
    }

    private Category checkCategory(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " not found"));
    }
}

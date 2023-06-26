package ru.practicum.ewmService.controller.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewmService.dto.category.NewCategoryDto;
import ru.practicum.ewmService.service.category.CategoryService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor
public class PublicCategoryController {
    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<NewCategoryDto>> findAllCategories(
            @RequestParam(required = false, defaultValue = "0") Integer from,
            @RequestParam(required = false, defaultValue = "10") Integer size) {

        log.info("Get categories from {}, size {}", from, size);
        return ResponseEntity.ok(categoryService.findAllCategories(from, size));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<NewCategoryDto> findCategoryById(@PathVariable("categoryId") Long categoryId) {
        log.info("Get category with id={}", categoryId);
        return ResponseEntity.ok(categoryService.findCategoryById(categoryId));
    }
}

package ru.practicum.ewmService.controller.category;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import ru.practicum.ewmService.dto.category.NewCategoryDto;
import ru.practicum.ewmService.service.category.CategoryService;

import javax.validation.Valid;

@Slf4j
@Validated
@Controller
@RequestMapping(path = "/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {
    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<NewCategoryDto> createCategory(@RequestBody @Valid NewCategoryDto dto) {
        log.info("Create category {}", dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(categoryService.createCategory(dto));
    }

    @DeleteMapping("{categoryId}")
    public ResponseEntity<Void> deleteCategoryById(@PathVariable("categoryId") Long categoryId) {
        log.info("Delete category with id = {}", categoryId);
        categoryService.deleteCategoryById(categoryId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("{categoryId}")
    public ResponseEntity<NewCategoryDto> updateCategory(@PathVariable("categoryId") Long categoryId,
                                                         @RequestBody @Valid NewCategoryDto dto) {
        log.info("Update category with id = {}, dto = {}", categoryId, dto);
        return ResponseEntity.ok(categoryService.updateCategory(categoryId, dto));
    }
}

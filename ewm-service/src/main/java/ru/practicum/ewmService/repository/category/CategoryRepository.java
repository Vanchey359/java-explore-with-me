package ru.practicum.ewmService.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmService.model.category.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {

}

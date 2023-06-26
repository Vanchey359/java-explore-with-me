package ru.practicum.ewmService.repository.category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmService.model.category.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

}

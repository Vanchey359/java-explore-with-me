package ru.practicum.ewmService.repository.location;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewmService.model.location.Location;

import java.util.List;

public interface LocationRepository extends JpaRepository<Location, Long> {
    List<Location> findByLatAndLon(Float lat, Float lon);
}

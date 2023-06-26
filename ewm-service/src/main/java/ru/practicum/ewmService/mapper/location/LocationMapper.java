package ru.practicum.ewmService.mapper.location;

import org.mapstruct.Mapper;
import ru.practicum.ewmService.dto.location.LocationDto;
import ru.practicum.ewmService.model.location.Location;

@Mapper(componentModel = "spring")
public interface LocationMapper {

    LocationDto toLocationDto(Location location);

    Location toLocation(LocationDto locationDto);
}

package ru.practicum.ewmService.mapper.compilation;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewmService.dto.compilation.CompilationDto;
import ru.practicum.ewmService.dto.compilation.NewCompilationDto;
import ru.practicum.ewmService.dto.event.EventShortDto;
import ru.practicum.ewmService.mapper.event.EventMapper;
import ru.practicum.ewmService.model.compilation.Compilation;
import ru.practicum.ewmService.model.event.Event;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = EventMapper.class)
public interface CompilationMapper {

    @Mapping(target = "id", source = "compilation.id")
    @Mapping(target = "events", source = "eventShortDtoList")
    @Mapping(target = "pinned", source = "compilation.pinned")
    @Mapping(target = "title", source = "compilation.title")
    CompilationDto toCompilationDto(Compilation compilation, List<EventShortDto> eventShortDtoList);

    default CompilationDto toCompilationDto(Compilation compilation) {
        return CompilationDto.builder()
                .id(compilation.getId())
                .events(compilation.getEvents().stream()
                        .map(this::toEventShortDto)
                        .collect(Collectors.toList()))
                .pinned(compilation.getPinned())
                .title(compilation.getTitle())
                .build();
    }

    @Mapping(target = "events", source = "eventList")
    @Mapping(target = "pinned", source = "dto.pinned")
    @Mapping(target = "title", source = "dto.title")
    Compilation toCompilation(NewCompilationDto dto, List<Event> eventList);

    default EventShortDto toEventShortDto(Event event) {
        return Mappers.getMapper(EventMapper.class).toEventShortDto(event);
    }
}

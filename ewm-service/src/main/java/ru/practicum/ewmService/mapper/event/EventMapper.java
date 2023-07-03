package ru.practicum.ewmService.mapper.event;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewmService.dto.category.NewCategoryDto;
import ru.practicum.ewmService.dto.event.EventFullDto;
import ru.practicum.ewmService.dto.event.EventShortDto;
import ru.practicum.ewmService.dto.event.NewEventDto;
import ru.practicum.ewmService.dto.location.LocationDto;
import ru.practicum.ewmService.dto.user.UserShortDto;
import ru.practicum.ewmService.mapper.category.CategoryMapper;
import ru.practicum.ewmService.mapper.location.LocationMapper;
import ru.practicum.ewmService.mapper.user.UserMapper;
import ru.practicum.ewmService.model.category.Category;
import ru.practicum.ewmService.model.event.Event;
import ru.practicum.ewmService.model.event.State;
import ru.practicum.ewmService.model.location.Location;
import ru.practicum.ewmService.model.user.User;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring", uses = {CategoryMapper.class, UserMapper.class, LocationMapper.class})
public interface EventMapper {

    default EventFullDto toEventFullDto(Event event) {
        return EventFullDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .createdOn(event.getCreatedOn())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .initiator(toUserShortDto(event.getInitiator()))
                .location(toLocationDto(event.getLocation()))
                .paid(event.getPaid())
                .participantLimit(event.getParticipantLimit())
                .publishedOn((event.getPublishedOn() == null) ? null : event.getPublishedOn())
                .requestModeration(event.getRequestModeration())
                .state(event.getState())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    default Event toEvent(NewEventDto newEventDto, Category category, User user, LocalDateTime dateTime) {
        return Event.builder()
                .annotation(newEventDto.getAnnotation())
                .category(category)
                .confirmedRequests(0L)
                .description(newEventDto.getDescription())
                .createdOn(dateTime)
                .eventDate(newEventDto.getEventDate())
                .initiator(user)
                .location(toLocation(newEventDto.getLocation()))
                .paid(newEventDto.getPaid())
                .participantLimit(newEventDto.getParticipantLimit())
                .requestModeration(newEventDto.getRequestModeration())
                .state(State.PENDING)
                .title(newEventDto.getTitle())
                .views(0L)
                .build();
    }

    default EventShortDto toEventShortDto(Event event) {
        return EventShortDto.builder()
                .id(event.getId())
                .annotation(event.getAnnotation())
                .category(toCategoryDto(event.getCategory()))
                .confirmedRequests(event.getConfirmedRequests())
                .eventDate(event.getEventDate())
                .initiator(toUserShortDto(event.getInitiator()))
                .paid(event.getPaid())
                .title(event.getTitle())
                .views(event.getViews())
                .build();
    }

    default NewCategoryDto toCategoryDto(Category category) {
        return Mappers.getMapper(CategoryMapper.class).toCategoryDto(category);
    }

    default UserShortDto toUserShortDto(User user) {
        return Mappers.getMapper(UserMapper.class).toUserShortDto(user);
    }

    default LocationDto toLocationDto(Location location) {
        return Mappers.getMapper(LocationMapper.class).toLocationDto(location);
    }

    default Location toLocation(LocationDto locationDto) {
        return Mappers.getMapper(LocationMapper.class).toLocation(locationDto);
    }
}

package ru.practicum.ewmService.service.event;

import ru.practicum.ewmService.dto.event.EventFullDto;
import ru.practicum.ewmService.dto.event.EventShortDto;
import ru.practicum.ewmService.dto.event.NewEventDto;
import ru.practicum.ewmService.dto.event.UpdateEventDto;
import ru.practicum.ewmService.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewmService.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewmService.dto.request.ParticipationRequestDto;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

public interface EventService {
    EventFullDto createEvent(Long userId, NewEventDto dto);

    List<EventShortDto> findEventsCreatedByUser(Long userId, Integer from, Integer size);

    EventFullDto findFullEventInfoByIdCreatedByUser(Long userId, Long eventId);

    EventFullDto updateEventCreatedByCurrentUser(Long userId, Long eventId, UpdateEventDto dto);

    List<EventFullDto> searchEventsForAdminWithFiltres(List<Long> users, List<String> states, List<Long> categories,
                                                       LocalDateTime rangeStart, LocalDateTime rangeEnd, Integer from, Integer size);

    EventFullDto updateByAdmin(Long eventId, UpdateEventDto dto);

    List<ParticipationRequestDto> findRequestsMadeByUserForEvent(Long userId, Long eventId);

    EventRequestStatusUpdateResult changeRequestsStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest dto);

    List<EventShortDto> getEventsWithFiltersPublic(String text, List<Long> categories, Boolean paid, LocalDateTime rangeStart,
                                                   LocalDateTime rangeEnd, Boolean onlyAvailable, String sort, Integer from,
                                                   Integer size, HttpServletRequest request);

    EventFullDto getEventWithFullInfoById(Long id, HttpServletRequest request);
}
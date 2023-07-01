package ru.practicum.ewmService.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewmService.dto.event.EventFullDto;
import ru.practicum.ewmService.dto.event.EventShortDto;
import ru.practicum.ewmService.dto.event.NewEventDto;
import ru.practicum.ewmService.dto.event.UpdateEventDto;
import ru.practicum.ewmService.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewmService.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewmService.dto.request.ParticipationRequestDto;
import ru.practicum.ewmService.service.event.EventService;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@Validated
@Controller
@RequestMapping(path = "/users/{userId}/events")
@RequiredArgsConstructor
public class PrivateEventsController {
    private final EventService eventsService;

    @PostMapping
    public ResponseEntity<EventFullDto> createEvent(@PathVariable("userId") Long userId,
                                                    @RequestBody @Valid NewEventDto dto) {
        log.info("Create event from userId {}, dto {}", userId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(eventsService.createEvent(userId, dto));
    }

    @GetMapping
    public ResponseEntity<List<EventShortDto>> findEventsCreatedByUser(
            @PathVariable("userId") Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {

        log.info("Get events from userId = {}, from {}, size{}", userId, from, size);
        return ResponseEntity.ok(eventsService.findEventsCreatedByUser(userId, from, size));
    }

    @GetMapping("/{eventId}")
    public ResponseEntity<EventFullDto> getFullEventByIdCreatedByUser(@PathVariable("userId") Long userId,
                                                                      @PathVariable("eventId") Long eventId) {
        log.info("Get event with id = {} from userId = {}", eventId, userId);
        return ResponseEntity.ok(eventsService.findFullEventInfoByIdCreatedByUser(userId, eventId));
    }

    @PatchMapping("/{eventId}")
    public ResponseEntity<EventFullDto> updateEventPrivate(@PathVariable("userId") Long userId,
                                                           @PathVariable("eventId") Long eventId,
                                                           @RequestBody @Valid UpdateEventDto dto) {
        log.info("Update event with id = {} from userId = {}, dto = {}", eventId, userId, dto);
        return ResponseEntity.ok(eventsService.updateEventCreatedByCurrentUser(userId, eventId, dto));
    }

    @GetMapping("/{eventId}/requests")
    public ResponseEntity<List<ParticipationRequestDto>> findRequestsMadeByUserForEvent(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId) {

        log.info("Get requests for userId = {}, for eventId = {}", userId, eventId);
        return ResponseEntity.ok(eventsService.findRequestsMadeByUserForEvent(userId, eventId));
    }

    @PatchMapping("/{eventId}/requests")
    public ResponseEntity<EventRequestStatusUpdateResult> changeRequestsStatus(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @RequestBody EventRequestStatusUpdateRequest dto) {

        log.info("Change requests status with userId = {}, eventId = {}, dto = {} ", userId, eventId, dto);
        return ResponseEntity.ok(eventsService.changeRequestsStatus(userId, eventId, dto));
    }
}

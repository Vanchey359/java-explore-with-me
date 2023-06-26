package ru.practicum.ewmService.controller.event;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewmService.dto.event.EventFullDto;
import ru.practicum.ewmService.dto.event.EventShortDto;
import ru.practicum.ewmService.service.event.EventService;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.List;

import static ru.practicum.ewmService.util.Util.YYYY_MM_DD_HH_MM_SS;

@Slf4j
@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class PublicEventController {
    private final EventService eventsService;

    @GetMapping
    public ResponseEntity<List<EventShortDto>> getEventsWithFilters(
            @RequestParam(name = "text", required = false) String text,
            @RequestParam(name = "categories", required = false) List<Long> categories,
            @RequestParam(name = "paid", required = false) Boolean paid,
            @RequestParam(name = "rangeStart", required = false) @DateTimeFormat(pattern = YYYY_MM_DD_HH_MM_SS) LocalDateTime rangeStart,
            @RequestParam(name = "rangeEnd", required = false) @DateTimeFormat(pattern = YYYY_MM_DD_HH_MM_SS) LocalDateTime rangeEnd,
            @RequestParam(name = "onlyAvailable", required = false, defaultValue = "false") Boolean onlyAvailable,
            @RequestParam(name = "sort", required = false) String sort,
            @RequestParam(name = "from", required = false, defaultValue = "0") Integer from,
            @RequestParam(name = "size", required = false, defaultValue = "10") Integer size,
            HttpServletRequest request) {

        log.info("Get events from text {}, categories {}, paid {}, rangeStart {}, rangeEnd {}, onlyAvailable {}, " +
                        "sort {}, from {}, size {}",
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size);

        return ResponseEntity.ok(eventsService.getEventsWithFiltersPublic(
                text, categories, paid, rangeStart, rangeEnd, onlyAvailable, sort, from, size, request));
    }

    @GetMapping("{eventId}")
    public ResponseEntity<EventFullDto> getEventWithFullInfoById(
            @PathVariable("eventId") Long eventId, HttpServletRequest request) {
        log.info("Get event with id={}", eventId);
        return ResponseEntity.ok(eventsService.getEventWithFullInfoById(eventId, request));
    }
}
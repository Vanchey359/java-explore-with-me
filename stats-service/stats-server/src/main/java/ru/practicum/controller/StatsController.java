package ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class StatsController {
    private final StatsService statsService;

    @PostMapping("/hit")
    public ResponseEntity<EndpointHitDto> addStatistic(@RequestBody EndpointHitDto endpointHitDto) {
        log.info("Adding endpointHitDto {}", endpointHitDto);
        return ResponseEntity.ok(statsService.addStatistic(endpointHitDto));
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ViewStatsDto>> getStatistic(
            @RequestParam(name = "start") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime start,
            @RequestParam(name = "end") @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime end,
            @RequestParam(name = "uris", required = false) List<String> uris,
            @RequestParam(name = "unique", defaultValue = "false") Boolean unique) {
        log.info("Getting statistic from start {}. end {}. uris {}, unique {}", start, end, uris, unique);
        return ResponseEntity.ok(statsService.getStatistic(start, end, uris, unique));
    }
}

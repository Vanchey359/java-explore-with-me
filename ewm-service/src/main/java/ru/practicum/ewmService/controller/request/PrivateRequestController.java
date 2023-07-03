package ru.practicum.ewmService.controller.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.ewmService.dto.request.ParticipationRequestDto;
import ru.practicum.ewmService.service.request.RequestServiceImpl;

import java.util.List;

@Slf4j
@Controller
@RequestMapping(path = "/users/{userId}/requests")
@RequiredArgsConstructor
public class PrivateRequestController {
    private final RequestServiceImpl requestService;

    @PostMapping
    public ResponseEntity<ParticipationRequestDto> createRequest(@PathVariable("userId") Long userId,
                                                                 @RequestParam("eventId") Long eventId) {
        log.info("Create request from userId = {}, eventId = {}", userId, eventId);
        return ResponseEntity.status(HttpStatus.CREATED).body(requestService.createRequestByUser(userId, eventId));
    }

    @GetMapping
    public ResponseEntity<List<ParticipationRequestDto>> getRequestsCreatedByUser(
            @PathVariable("userId") Long userId) {
        log.info("Get requests for userId = {}", userId);
        return ResponseEntity.ok(requestService.getRequestsCreatedByUser(userId));
    }

    @PatchMapping("/{requestId}/cancel")
    public ResponseEntity<ParticipationRequestDto> cancelRequest(@PathVariable("userId") Long userId,
                                                                 @PathVariable("requestId") Long requestId) {
        log.info("Cancel request with userId = {}, requestId = {}", userId, requestId);
        return ResponseEntity.ok(requestService.cancelOwnRequestCreatedByUser(userId, requestId));
    }

}

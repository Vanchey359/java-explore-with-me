package ru.practicum.ewmService.controller.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewmService.dto.comment.CommentDto;
import ru.practicum.ewmService.service.comment.CommentService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/comments")
@RequiredArgsConstructor
public class PublicCommentController {
    private final CommentService service;

    @GetMapping("/{id}")
    public ResponseEntity<CommentDto> getComment(@PathVariable Long id) {
        log.info("Get comment with id {}", id);
        return ResponseEntity.ok(service.findCommentById(id));
    }

    @GetMapping("/event/{eventId}")
    public ResponseEntity<List<CommentDto>> getCommentForEvent(
            @PathVariable(name = "eventId") Long eventId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {

        log.info("Get comments for event: eventId {}, from {}, size {}", eventId, from, size);
        return ResponseEntity.ok(service.getCommentForEventById(eventId, PageRequest.of(from, size)));
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getComments(
            @RequestParam(required = false, name = "text") String text,
            @RequestParam(required = false, name = "eventId") Long eventId,
            @RequestParam(required = false, name = "userId") Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {

        log.info("Get comments by Filters text {}, idEvent {}, idUser {}, from {}, size {}",
                text, eventId, userId, from, size);
        return ResponseEntity.ok(service.getCommentsByFilters(text, eventId, userId, PageRequest.of(from, size)));
    }
}

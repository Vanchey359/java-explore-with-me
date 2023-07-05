package ru.practicum.ewmService.controller.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewmService.dto.comment.CommentDto;
import ru.practicum.ewmService.dto.comment.NewCommentDto;
import ru.practicum.ewmService.service.comment.CommentService;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/users/{userId}/events/{eventId}/comments")
@RequiredArgsConstructor
public class PrivateCommentController {
    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> createComment(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @RequestBody @Valid NewCommentDto commentDto) {

        log.info("Create Comment from userId {}, eventId {}, commentDto {}", userId, eventId, commentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(commentService.createCommentForEvent(userId, eventId, commentDto));
    }

    @DeleteMapping("{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteComment(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @PathVariable("commentId") Long commentId) {

        log.info("Delete comment with userId {}, eventId {}, commentId {}", userId, eventId, commentId);
        commentService.deleteCommentByUser(userId, eventId, commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable("userId") Long userId,
            @PathVariable("eventId") Long eventId,
            @PathVariable("commentId") Long commentId,
            @RequestBody @Valid NewCommentDto commentDto) {

        log.info("Update comment with userId {}, eventId {}, commentId {}, commentDto {}",
                userId, eventId, commentId, commentDto);
        return ResponseEntity.ok(commentService.updateCommentByUser(userId, eventId, commentId, commentDto));
    }
}
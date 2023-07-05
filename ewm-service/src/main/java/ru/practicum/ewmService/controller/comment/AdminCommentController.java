package ru.practicum.ewmService.controller.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.ewmService.dto.comment.CommentDto;
import ru.practicum.ewmService.dto.comment.NewCommentDto;
import ru.practicum.ewmService.service.comment.CommentService;

import javax.validation.Valid;

@Slf4j
@Validated
@RestController
@RequestMapping(path = "/admin/comments")
@RequiredArgsConstructor
public class AdminCommentController {
    private final CommentService commentService;

    @DeleteMapping("{commentId}")
    public ResponseEntity<Void> deleteCommentByAdmin(@PathVariable("commentId") Long commentId) {
        log.info("Delete comment by admin with commentId {}", commentId);
        commentService.deleteCommentByAdmin(commentId);

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping("{commentId}")
    public ResponseEntity<CommentDto> updateCommentByAdmin(@PathVariable("commentId") Long commentId,
                                                           @RequestBody @Valid NewCommentDto commentDto) {
        log.info("Update comment by admin with commentId {}, commentDto {}", commentId, commentDto);
        return ResponseEntity.ok(commentService.updateCommentByAdmin(commentId, commentDto));
    }
}
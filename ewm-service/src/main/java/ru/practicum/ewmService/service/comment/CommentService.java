package ru.practicum.ewmService.service.comment;

import org.springframework.data.domain.Pageable;
import ru.practicum.ewmService.dto.comment.CommentDto;
import ru.practicum.ewmService.dto.comment.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createCommentForEvent(Long userId, Long eventId, NewCommentDto commentDto);

    CommentDto findCommentById(Long commentId);

    void deleteCommentByUser(Long userId, Long eventId, Long id);

    CommentDto updateCommentByUser(Long userId, Long eventId, Long commentId, NewCommentDto commentDto);

    List<CommentDto> getCommentForEventById(Long eventId, Pageable pageable);

    List<CommentDto> getCommentsByFilters(String text, Long eventId, Long userId, Pageable pageable);

    void deleteCommentByAdmin(Long id);

    CommentDto updateCommentByAdmin(Long commentId, NewCommentDto commentDto);
}
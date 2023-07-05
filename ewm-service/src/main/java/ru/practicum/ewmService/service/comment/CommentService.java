package ru.practicum.ewmService.service.comment;

import ru.practicum.ewmService.dto.comment.CommentDto;
import ru.practicum.ewmService.dto.comment.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto createCommentForEvent(Long userId, Long eventId, NewCommentDto commentDto);

    CommentDto findCommentById(Long idComment);

    void deleteCommentByUser(Long userId, Long eventId, Long id);

    CommentDto updateCommentByUser(Long userId, Long eventId, Long commentId, NewCommentDto commentDto);

    List<CommentDto> getCommentForEventById(Long idEvent, Integer from, Integer size);

    List<CommentDto> getCommentsByFilters(String text, Long idEvent, Long idUser, Integer from, Integer size);

    void deleteCommentByAdmin(Long id);

    CommentDto updateCommentByAdmin(Long commentId, NewCommentDto commentDto);
}
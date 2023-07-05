package ru.practicum.ewmService.service.comment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmService.dto.comment.CommentDto;
import ru.practicum.ewmService.dto.comment.NewCommentDto;
import ru.practicum.ewmService.exception.ConflictException;
import ru.practicum.ewmService.exception.NotFoundException;
import ru.practicum.ewmService.mapper.comment.CommentMapper;
import ru.practicum.ewmService.model.comment.Comment;
import ru.practicum.ewmService.model.event.Event;
import ru.practicum.ewmService.model.event.State;
import ru.practicum.ewmService.model.user.User;
import ru.practicum.ewmService.repository.comment.CommentRepository;
import ru.practicum.ewmService.repository.event.EventRepository;
import ru.practicum.ewmService.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentMapper commentMapper;

    @Transactional
    public CommentDto createCommentForEvent(Long userId, Long eventId, NewCommentDto commentDto) {
        User user = checkUserInDb(userId);
        Event event = findEventById(eventId);

        if (!event.getState().equals(State.PUBLISHED)) {
            throw new ConflictException("Cannot add comment: event with id=" + eventId +
                    " is not published yet");
        }

        Comment eventComment = commentMapper.toComment(commentDto, event, user, LocalDateTime.now());
        Comment newEventComment = commentRepository.save(eventComment);
        log.info("Created new comment by userId = {} for event with id = {}", userId, eventId);

        return commentMapper.toCommentDto(newEventComment);
    }

    public CommentDto findCommentById(Long commentId) {
        Comment eventComment = getCommentById(commentId);
        log.info("Found comment with id = {}", commentId);

        return commentMapper.toCommentDto(eventComment);
    }

    @Transactional
    public void deleteCommentByUser(Long userId, Long eventId, Long commentId) {
        User user = checkUserAndEvent(userId, eventId);
        Comment eventComment = getCommentById(commentId);

        if (!eventComment.getAuthor().getId().equals(user.getId())) {
            throw new ConflictException("Only the author may delete comment with id=" + commentId +
                    ". User with id=" + userId + " is not the author");
        }
        log.info("Deleted comment with id = {}", commentId);

        commentRepository.deleteById(commentId);
    }

    @Transactional
    public CommentDto updateCommentByUser(Long userId, Long eventId, Long commentId, NewCommentDto commentDto) {
        User user = checkUserAndEvent(userId, eventId);
        Comment eventComment = getCommentById(commentId);

        if (!eventComment.getAuthor().getId().equals(user.getId())) {
            throw new ConflictException("Only the author may update comment with id=" + commentId +
                    ". User with id=" + userId + " is not the author");
        }

        eventComment.setText(commentDto.getText());
        Comment updatedEventComment = commentRepository.save(eventComment);
        log.info("Updated comment with id = {} by userId = {} for eventId = {}", commentId, userId, eventId);

        return commentMapper.toCommentDto(updatedEventComment);
    }

    public List<CommentDto> getCommentForEventById(Long eventId, Integer from, Integer size) {
        Event event = findEventById(eventId);
        PageRequest page = PageRequest.of(from, size);
        List<Comment> commentList = commentRepository.findByEvent(event, page);
        log.info("Found comments for eventId = {}", eventId);

        return commentList.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    public List<CommentDto> getCommentsByFilters(String text, Long eventId, Long userId, Integer from, Integer size) {
        User user = null;
        Event event = null;
        PageRequest page = PageRequest.of(from, size);

        if (userId != null) {
            user = checkUserInDb(userId);
        }
        if (eventId != null) {
            event = findEventById(eventId);
        }

        List<Comment> eventComments = commentRepository.getCommentsByFilters(text, user, event, page);
        log.info("Found comments by filters for eventId = {}", eventId);

        return eventComments.stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteCommentByAdmin(Long commentId) {
        commentRepository.deleteById(commentId);
        log.info("Deleted comment with id = {} by admin", commentId);
    }

    @Transactional
    public CommentDto updateCommentByAdmin(Long commentId, NewCommentDto commentDto) {
        Comment eventComment = getCommentById(commentId);

        eventComment.setText(commentDto.getText());
        Comment updatedEventComment = commentRepository.save(eventComment);
        log.info("Updated comment with id = {} by admin", commentId);

        return commentMapper.toCommentDto(updatedEventComment);
    }

    private User checkUserInDb(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
    }

    private Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));
    }

    private User checkUserAndEvent(Long userId, Long eventId) {
        findEventById(eventId);
        return checkUserInDb(userId);
    }

    private Comment getCommentById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment with id=" + commentId + " not found"));
    }
}
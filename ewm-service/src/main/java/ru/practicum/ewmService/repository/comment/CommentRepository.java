package ru.practicum.ewmService.repository.comment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewmService.model.comment.Comment;
import ru.practicum.ewmService.model.event.Event;
import ru.practicum.ewmService.model.user.User;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findByEvent(Event event, Pageable pageable);

    @Query("select c from Comment as c " +
            "where (:text is null or upper(c.text) like upper(concat('%', :text, '%'))) " +
            "and (:user is null or c.author = :user) " +
            "and (:event is null or c.event = :event) " +
            "order by c.created")
    List<Comment> getCommentsByFilters(@Param("text") String text,
                                       @Param("user") User user,
                                       @Param("event") Event event,
                                       Pageable pageable);
}
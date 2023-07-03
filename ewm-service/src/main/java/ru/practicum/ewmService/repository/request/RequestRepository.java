package ru.practicum.ewmService.repository.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewmService.model.request.Request;
import ru.practicum.ewmService.model.user.User;

import java.util.List;

public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByRequester(User user);

    List<Request> findByEventId(Long eventId);

    @Query("SELECT r FROM Request r WHERE r.id IN (:ids)")
    List<Request> findListRequestsById(@Param("ids") List<Long> ids);

    @Query("select count(r) from Request r where eventId = :eventId and status = 'CONFIRMED'")
    Long findConfirmedRequestsByEvent(@Param("eventId") Long eventId);
}

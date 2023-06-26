package ru.practicum.ewmService.repository.request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.practicum.ewmService.model.request.Request;
import ru.practicum.ewmService.model.user.User;

import java.util.List;

@Repository
public interface RequestRepository extends JpaRepository<Request, Long> {

    List<Request> findByRequester(User user);

    List<Request> findByEventId(Long eventId);
}

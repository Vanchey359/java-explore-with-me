package ru.practicum.ewmService.service.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewmService.dto.request.ParticipationRequestDto;
import ru.practicum.ewmService.exception.BadRequestException;
import ru.practicum.ewmService.exception.ConflictException;
import ru.practicum.ewmService.exception.NotFoundException;
import ru.practicum.ewmService.mapper.request.RequestMapper;
import ru.practicum.ewmService.model.event.Event;
import ru.practicum.ewmService.model.event.State;
import ru.practicum.ewmService.model.request.Request;
import ru.practicum.ewmService.model.user.User;
import ru.practicum.ewmService.repository.event.EventRepository;
import ru.practicum.ewmService.repository.request.RequestRepository;
import ru.practicum.ewmService.repository.user.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final RequestMapper requestMapper;

    @Transactional
    public ParticipationRequestDto createRequestByUser(Long userId, Long eventId) {
        if (userId == null || eventId == null) {
            throw new BadRequestException("Bad request: userId or eventId is null");
        }

        Request request;
        User user = checkUserInDb(userId);
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));

        if (event.getInitiator().getId().equals(userId)) {
            throw new ConflictException("Initiator with id=" + userId + " " +
                    "cannot request participation in own event with id=" + eventId);
        }
        if (event.getState().equals(State.PENDING) || event.getState().equals(State.CANCELED)) {
            throw new ConflictException("Cannot participate in unpublished event with id=" + eventId);
        }
        if (event.getParticipantLimit() != 0 && event.getParticipantLimit() <= event.getConfirmedRequests())
            throw new ConflictException("Event with id=" + eventId + " has reached the participation limit");

        if (!event.getRequestModeration() || event.getParticipantLimit() == 0) {
            request = Request.builder()
                    .eventId(eventId)
                    .created(LocalDateTime.now())
                    .requester(user)
                    .status(State.CONFIRMED)
                    .build();

            event.setConfirmedRequests(event.getConfirmedRequests() + 1);
            eventRepository.save(event);
        } else {
            request = Request.builder()
                    .eventId(eventId)
                    .created(LocalDateTime.now())
                    .requester(user)
                    .status(State.PENDING)
                    .build();
        }

        log.info("Created request by userId={}, eventId={}", userId, eventId);

        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    public List<ParticipationRequestDto> getRequestsCreatedByUser(Long userId) {
        User user = checkUserInDb(userId);
        List<Request> requests = requestRepository.findByRequester(user);
        List<ParticipationRequestDto> participationRequestDtoList = requests.stream()
                .map(requestMapper::toRequestDto)
                .collect(Collectors.toList());
        log.info("Found requests (size={} created by userId={}", participationRequestDtoList.size(), userId);

        return participationRequestDtoList;
    }

    @Transactional
    public ParticipationRequestDto cancelOwnRequestCreatedByUser(Long userId, Long requestId) {
        checkUserInDb(userId);
        Request request = requestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " not found"));
        request.setStatus(State.CANCELED);
        log.info("Cancelled request with id={} created by userId={}", requestId, userId);

        return requestMapper.toRequestDto(requestRepository.save(request));
    }

    private User checkUserInDb(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + "not found"));
    }
}

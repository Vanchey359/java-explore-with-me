package ru.practicum.ewmService.service.event;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.client.StatsClient;
import ru.practicum.dto.EndpointHitDto;
import ru.practicum.dto.ViewStatsDto;
import ru.practicum.ewmService.dto.event.EventFullDto;
import ru.practicum.ewmService.dto.event.EventShortDto;
import ru.practicum.ewmService.dto.event.NewEventDto;
import ru.practicum.ewmService.dto.event.UpdateEventDto;
import ru.practicum.ewmService.dto.location.LocationDto;
import ru.practicum.ewmService.dto.request.EventRequestStatusUpdateRequest;
import ru.practicum.ewmService.dto.request.EventRequestStatusUpdateResult;
import ru.practicum.ewmService.dto.request.ParticipationRequestDto;
import ru.practicum.ewmService.exception.BadRequestException;
import ru.practicum.ewmService.exception.BadStateException;
import ru.practicum.ewmService.exception.ConflictException;
import ru.practicum.ewmService.exception.NotFoundException;
import ru.practicum.ewmService.mapper.event.EventMapper;
import ru.practicum.ewmService.mapper.location.LocationMapper;
import ru.practicum.ewmService.mapper.request.RequestMapper;
import ru.practicum.ewmService.model.category.Category;
import ru.practicum.ewmService.model.event.Event;
import ru.practicum.ewmService.model.event.Sort;
import ru.practicum.ewmService.model.event.State;
import ru.practicum.ewmService.model.event.StateAction;
import ru.practicum.ewmService.model.request.Request;
import ru.practicum.ewmService.model.user.User;
import ru.practicum.ewmService.repository.category.CategoryRepository;
import ru.practicum.ewmService.repository.event.EventRepository;
import ru.practicum.ewmService.repository.location.LocationRepository;
import ru.practicum.ewmService.repository.request.RequestRepository;
import ru.practicum.ewmService.repository.user.UserRepository;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;
import static ru.practicum.ewmService.util.Util.DATE_TIME_FORMATTER;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
@ComponentScan(basePackages = {"ru.practicum.client"})
public class EventServiceImpl implements EventService {
    private final EventRepository eventRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;
    private final LocationRepository locationRepository;
    private final RequestRepository requestRepository;
    private final StatsClient statClient;
    private final EventMapper eventMapper;
    private final RequestMapper requestMapper;
    private final LocationMapper locationMapper;

    @Transactional
    public EventFullDto createEvent(Long userId, NewEventDto dto) {
        if (dto.getPaid() == null) {
            dto.setPaid(false);
        }
        if (dto.getParticipantLimit() == null) {
            dto.setParticipantLimit(0L);
        }
        if (dto.getRequestModeration() == null) {
            dto.setRequestModeration(true);
        }

        LocalDateTime nowDateTime = LocalDateTime.now();
        checkDateTimeForDto(nowDateTime, dto.getEventDate());

        Category category = getCategoryById(dto.getCategory());
        User user = getUserById(userId);

        locationRepository.save(locationMapper.toLocation(getLocationFromDto(dto)));
        Event event = eventMapper.toEvent(dto, category, user, nowDateTime);
        event.setState(State.PENDING); // See EventMapper.toEvent()
        Event result = eventRepository.save(event);
        log.info("Created new event=" + event);

        return eventMapper.toEventFullDto(result);
    }

    public List<EventShortDto> findEventsCreatedByUser(Long userId, Integer from, Integer size) {
        PageRequest page = PageRequest.of(from, size);
        User user = checkUserInDb(userId);

        List<Event> events = eventRepository.findDByInitiator(user, page);
        Map<Long, Long> hits = getStatisticFromListEvents(events);
        events.forEach(event -> event.setViews(hits.get(event.getId())));

        List<EventShortDto> eventShortDtoList = events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
        log.info("Found events with short info created by userId=" + userId + ", events size=" + eventShortDtoList.size());

        return eventShortDtoList;
    }

    public EventFullDto findFullEventInfoByIdCreatedByUser(Long userId, Long eventId) {
        checkUserInDb(userId);

        Event event = getEventById(eventId);
        Map<Long, Long> hits = getStatisticFromListEvents(List.of(event));
        event.setViews(hits.get(event.getId()));
        log.info("Found event with full info created by userId=" + userId + ", eventId=" + eventId);

        return eventMapper.toEventFullDto(event);
    }

    @Transactional
    public EventFullDto updateEventCreatedByCurrentUser(Long userId, Long eventId, UpdateEventDto dto) {
        Event event = getEventById(eventId);
        checkUserInDb(userId);

        if (dto.getEventDate() != null) {
            checkDateTimeForDto(LocalDateTime.now(), dto.getEventDate());
        }
        if (!(event.getState().equals(State.CANCELED) || event.getState().equals(State.PENDING))) {
            throw new BadStateException("Invalid state=" + event.getState() + "." +
                    " It allowed to change events with CANCELED ot PENDING state");
        }
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case SEND_TO_REVIEW:
                    event.setState(State.PENDING);
                    break;
                case CANCEL_REVIEW:
                    event.setState(State.CANCELED);
                    break;
                default:
                    throw new BadStateException("Invalid StateAction status");
            }
        }

        Event updatedEvent = updateEventFields(event, dto);
        Event result = eventRepository.save(updatedEvent);
        locationRepository.save(result.getLocation());
        Map<Long, Long> hits = getStatisticFromListEvents(List.of(result));
        event.setViews(hits.get(event.getId()));
        log.info("Updated event with id=" + eventId + " created by user with id=" + userId);

        return eventMapper.toEventFullDto(result);
    }

    public List<EventFullDto> searchEventsForAdminWithFiltres(
            List<Long> users,
            List<String> states,
            List<Long> categories,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Integer from,
            Integer size) {

        PageRequest page = PageRequest.of(from, size);
        List<State> stateList = null;
        LocalDateTime start = null;
        LocalDateTime end = null;

        if (states != null) {
            stateList = states.stream()
                    .map(State::valueOf)
                    .collect(Collectors.toList());
        }
        if (rangeStart != null) {
            start = rangeStart;
        }
        if (rangeEnd != null) {
            end = rangeEnd;
        }

        List<Event> events = eventRepository.getEventsWithUsersStatesCategoriesDateTime(
                users, stateList, categories, start, end, page);
        Map<Long, Long> hits = getStatisticFromListEvents(events);
        events.forEach(event -> event.setViews(hits.get(event.getId())));
        List<EventFullDto> eventFullDtoList = events.stream()
                .map(eventMapper::toEventFullDto)
                .collect(Collectors.toList());
        log.info("Found events for admin, size=" + eventFullDtoList.size());

        return eventFullDtoList;
    }

    @Transactional
    public EventFullDto updateByAdmin(Long eventId, UpdateEventDto dto) {
        Event event = getEventById(eventId);

        if (dto.getEventDate() != null) {
            if (LocalDateTime.now().plusHours(1).isAfter(dto.getEventDate())) {
                throw new BadRequestException("Error. " +
                        "Datetime of event cannot be earlier than one hour before present time");
            }
        } else {
            if (dto.getStateAction() != null) {
                if (dto.getStateAction().equals(StateAction.PUBLISH_EVENT) &&
                        LocalDateTime.now().plusHours(1).isAfter(event.getEventDate())) {
                    throw new BadStateException("Error. " +
                            "Datetime of event cannot be earlier than one hour before present time");
                }
                if (dto.getStateAction().equals(StateAction.PUBLISH_EVENT) && !(event.getState().equals(State.PENDING))) {
                    throw new BadStateException("Invalid StateAction. Event can be published only with PENDING state");
                }
                if (dto.getStateAction().equals(StateAction.REJECT_EVENT) && event.getState().equals(State.PUBLISHED)) {
                    throw new BadStateException("Invalid StateAction. Event can be cancelled only with unpublished state");
                }
            }
        }
        if (dto.getStateAction() != null) {
            switch (dto.getStateAction()) {
                case REJECT_EVENT:
                    event.setState(State.CANCELED);
                    break;
                case PUBLISH_EVENT:
                    event.setState(State.PUBLISHED);
                    event.setPublishedOn(LocalDateTime.now());
                    break;
                default:
                    throw new BadStateException("Invalid StateAction of dto.");
            }
        }

        Event updatedEvent = updateEventFields(event, dto);
        Event updatedEventFromDB = eventRepository.save(updatedEvent);
        Map<Long, Long> hits = getStatisticFromListEvents(List.of(updatedEventFromDB));
        updatedEventFromDB.setViews(hits.get(event.getId()));

        log.info("Updated event with id={} by admin", eventId);

        return eventMapper.toEventFullDto(updatedEventFromDB);
    }

    @Transactional
    public List<EventShortDto> getEventsWithFiltersPublic(
            String text,
            List<Long> categories,
            Boolean paid,
            LocalDateTime rangeStart,
            LocalDateTime rangeEnd,
            Boolean onlyAvailable,
            String sort,
            Integer from,
            Integer size, HttpServletRequest request) {

        PageRequest page = PageRequest.of(from, size);
        List<Event> events = new ArrayList<>();
        checkDateTime(rangeStart, rangeEnd);

        if (onlyAvailable) {
            if (sort == null) {
                events = eventRepository.getAvailableEventsWithFiltersDateSorted(
                        text, State.PUBLISHED, categories, paid, rangeStart, rangeEnd, page);
            } else {
                switch (Sort.valueOf(sort)) {
                    case EVENT_DATE:
                        events = eventRepository.getAvailableEventsWithFiltersDateSorted(
                                text, State.PUBLISHED, categories, paid, rangeStart, rangeEnd, page);
                        addStatistic(request);
                        return events.stream()
                                .map(eventMapper::toEventShortDto)
                                .collect(Collectors.toList());
                    case VIEWS:
                        events = eventRepository.getAvailableEventsWithFilters(
                                text, State.PUBLISHED, categories, paid, rangeStart, rangeEnd, page);
                        Map<Long, Long> hits = getStatisticFromListEvents(events);
                        events.forEach(event -> event.setViews(hits.get(event.getId())));
                        addStatistic(request);
                        return events.stream()
                                .map(eventMapper::toEventShortDto)
                                .sorted(Comparator.comparing(EventShortDto::getViews))
                                .collect(Collectors.toList());
                }
            }
        } else {
            if (sort == null) {
                events = eventRepository.getAllEventsWithFiltersDateSorted(
                        text, State.PUBLISHED, categories, paid, rangeStart, rangeEnd, page);
            } else {
                switch (Sort.valueOf(sort)) {
                    case EVENT_DATE:
                        events = eventRepository.getAllEventsWithFiltersDateSorted(
                                text, State.PUBLISHED, categories, paid, rangeStart, rangeEnd, page);
                        addStatistic(request);
                        return events.stream()
                                .map(eventMapper::toEventShortDto)
                                .collect(Collectors.toList());
                    case VIEWS:
                        events = eventRepository.getAllEventsWithFilters(
                                text, State.PUBLISHED, categories, paid, rangeStart, rangeEnd, page);
                        Map<Long, Long> hits = getStatisticFromListEvents(events);
                        events.forEach(event -> event.setViews(hits.get(event.getId())));
                        addStatistic(request);
                        return events.stream()
                                .map(eventMapper::toEventShortDto)
                                .sorted(Comparator.comparing(EventShortDto::getViews))
                                .collect(Collectors.toList());
                }
            }
        }

        addStatistic(request);
        log.info("Found events with filters (public access)");

        return events.stream()
                .map(eventMapper::toEventShortDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventFullDto getEventWithFullInfoById(Long eventId, HttpServletRequest request) {
        Event event = getEventById(eventId);
        if (!event.getState().equals(State.PUBLISHED)) {
            throw new NotFoundException("Event is not published");
        }
        eventRepository.save(event);
        addStatistic(request);
        Map<Long, Long> hits = getStatisticFromListEvents(List.of(event));
        event.setViews(hits.get(event.getId()));
        log.info("Found event with full info, eventId={}", eventId);

        return eventMapper.toEventFullDto(event);
    }

    public List<ParticipationRequestDto> findRequestsMadeByUserForEvent(Long userId, Long eventId) {
        checkUserInDb(userId);
        getEventById(eventId);

        List<Request> requests = requestRepository.findByEventId(eventId);
        List<ParticipationRequestDto> participationRequestDtoList = requests.stream()
                .map(requestMapper::toRequestDto)
                .collect(Collectors.toList());
        log.info("Found requests made by userId={} for the eventId={}", userId, eventId);

        return participationRequestDtoList;
    }

    @Transactional
    public EventRequestStatusUpdateResult changeRequestsStatus(Long userId, Long eventId, EventRequestStatusUpdateRequest dto) {
        List<ParticipationRequestDto> confirmedRequests = new ArrayList<>();
        List<ParticipationRequestDto> rejectedRequests = new ArrayList<>();

        checkUserInDb(userId);
        Event event = getEventById(eventId);

        if (!event.getRequestModeration() || event.getParticipantLimit().equals(0L)) {
            throw new ConflictException("Confirmation is not necessary");
        }

        long limitBalance = event.getParticipantLimit() - event.getConfirmedRequests();
        if (event.getParticipantLimit() != 0 && limitBalance <= 0) {
            throw new ConflictException("Event has reached participation limit");
        }
        if (dto.getStatus().equals(State.REJECTED.toString())) {
            for (Long requestId : dto.getRequestIds()) {
                Request request = requestRepository.findById(requestId)
                        .orElseThrow(() -> new NotFoundException("Request with id=" + requestId + " not found"));
                if (request.getStatus().equals(State.PENDING)) {
                    request.setStatus(State.REJECTED);
                    requestRepository.save(request);
                    rejectedRequests.add(requestMapper.toRequestDto(request));
                }
            }
        }

        for (int i = 0; i < dto.getRequestIds().size(); i++) {
            if (limitBalance != 0) {
                int finalI1 = i;
                Request request = requestRepository.findById(dto.getRequestIds().get(i))
                        .orElseThrow(() -> new NotFoundException("Request with id=" + finalI1 + " not found"));
                if (request.getStatus().equals(State.PENDING)) {
                    request.setStatus(State.CONFIRMED);
                    event.setConfirmedRequests(event.getConfirmedRequests() + 1);
                    eventRepository.save(event);
                    requestRepository.save(request);
                    confirmedRequests.add(requestMapper.toRequestDto(request));
                    limitBalance--;
                }
            } else {
                int finalI2 = i;
                Request request = requestRepository.findById(dto.getRequestIds().get(i))
                        .orElseThrow(() -> new NotFoundException("Request with id=" + finalI2 + " not found"));
                if (request.getStatus().equals(State.PENDING)) {
                    request.setStatus(State.REJECTED);
                    requestRepository.save(request);
                    rejectedRequests.add(requestMapper.toRequestDto(request));
                }
            }
        }

        log.info("Changed request's status");

        return EventRequestStatusUpdateResult.builder()
                .confirmedRequests(confirmedRequests)
                .rejectedRequests(rejectedRequests)
                .build();
    }

    private void addStatistic(HttpServletRequest request) {
        String app = "main-service";

        statClient.addStatistic(EndpointHitDto.builder()
                .app(app)
                .uri(request.getRequestURI())
                .ip(request.getRemoteAddr())
                .timestamp(LocalDateTime.now().format(DATE_TIME_FORMATTER))
                .build());
    }

    private Map<Long, Long> getStatisticFromListEvents(List<Event> events) {
        List<Long> idEvents = events.stream()
                .map(Event::getId)
                .collect(Collectors.toList());

        String start = LocalDateTime.now().minusYears(100).format(DATE_TIME_FORMATTER);
        String end = LocalDateTime.now().format(DATE_TIME_FORMATTER);
        String eventsUri = "/events/";
        List<String> uris = idEvents.stream().map(id -> eventsUri + id).collect(Collectors.toList());
        ResponseEntity<Object> response = statClient.getStatistic(start, end, uris, true);
        ObjectMapper objectMapper = new ObjectMapper();
        List<ViewStatsDto> viewStatsDto = objectMapper.convertValue(response.getBody(), new TypeReference<>() {
        });
        Map<Long, Long> hits = new HashMap<>();

        for (ViewStatsDto statsDto : viewStatsDto) {
            String uri = statsDto.getUri();
            hits.put(Long.parseLong(uri.substring(eventsUri.length())), statsDto.getHits());
        }

        return hits;
    }

    private void checkDateTime(LocalDateTime start, LocalDateTime end) {
        if (start == null) {
            start = LocalDateTime.now().minusYears(100);
        }
        if (end == null) {
            end = LocalDateTime.now();
        }
        if (start.isAfter(end)) {
            throw new BadRequestException("Invalid request. The end date of the event is set later than the start date");
        }
    }

    private Event updateEventFields(Event event, UpdateEventDto dto) {
        ofNullable(dto.getAnnotation()).ifPresent(event::setAnnotation);

        ofNullable(dto.getCategory()).ifPresent(category -> event.setCategory(categoryRepository.findById(category)
                .orElseThrow(() -> new NotFoundException("CategoryId not found"))));

        ofNullable(dto.getDescription()).ifPresent(event::setDescription);
        ofNullable(dto.getEventDate()).ifPresent(event::setEventDate);
        ofNullable(dto.getLocation()).ifPresent(locationDto ->
                event.setLocation(locationMapper.toLocation(locationDto)));

        ofNullable(dto.getPaid()).ifPresent(event::setPaid);
        ofNullable(dto.getParticipantLimit()).ifPresent(event::setParticipantLimit);
        ofNullable(dto.getRequestModeration()).ifPresent(event::setRequestModeration);
        ofNullable(dto.getTitle()).ifPresent(event::setTitle);

        return event;
    }

    private void checkDateTimeForDto(LocalDateTime nowDateTime, LocalDateTime dtoDateTime) {
        if (nowDateTime.plusHours(2).isAfter(dtoDateTime)) {
            throw new BadRequestException("Error. Datetime of event cannot be earlier than 2 hours after current time");
        }
    }

    private User checkUserInDb(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
    }

    private Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " not found"));
    }

    private Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Category with id=" + categoryId + " not found"));
    }

    private User getUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User with id=" + userId + " not found"));
    }

    private LocationDto getLocationFromDto(NewEventDto dto) {
        if (dto.getLocation() == null) {
            return null;
        } else {
            return dto.getLocation();
        }
    }
}

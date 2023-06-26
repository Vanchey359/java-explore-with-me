package ru.practicum.ewmService.service.request;

import ru.practicum.ewmService.dto.request.ParticipationRequestDto;

import java.util.List;

public interface RequestService {
    ParticipationRequestDto createRequestByUser(Long userId, Long requestDto);

    List<ParticipationRequestDto> getRequestsCreatedByUser(Long userId);

    ParticipationRequestDto cancelOwnRequestCreatedByUser(Long userId, Long requestId);
}

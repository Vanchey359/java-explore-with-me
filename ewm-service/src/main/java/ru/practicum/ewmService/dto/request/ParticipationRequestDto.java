package ru.practicum.ewmService.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.ewmService.model.event.State;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class ParticipationRequestDto {

    private Long id;

    private Long event;

    private String created;

    private Long requester;

    private State status;

}

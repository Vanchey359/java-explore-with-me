package ru.practicum.ewmService.dto.compilation;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.ewmService.dto.event.EventShortDto;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class CompilationDto {

    private Long id;

    private List<EventShortDto> events;

    private Boolean pinned;

    private String title;
}

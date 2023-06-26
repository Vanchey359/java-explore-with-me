package ru.practicum.ewmService.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import ru.practicum.ewmService.dto.category.NewCategoryDto;
import ru.practicum.ewmService.dto.user.UserShortDto;

import java.time.LocalDateTime;

import static ru.practicum.ewmService.util.Util.YYYY_MM_DD_HH_MM_SS;

@Getter
@Setter
@Builder
@AllArgsConstructor
@RequiredArgsConstructor
public class EventShortDto {

    private Long id;

    private String annotation;

    private NewCategoryDto category;

    private Long confirmedRequests;

    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime eventDate;

    private UserShortDto initiator;

    private Boolean paid;

    private String title;

    private Long views;

}

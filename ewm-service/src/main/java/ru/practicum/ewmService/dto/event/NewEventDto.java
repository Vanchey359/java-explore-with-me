package ru.practicum.ewmService.dto.event;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;
import ru.practicum.ewmService.dto.location.LocationDto;

import javax.validation.constraints.NotNull;

import java.time.LocalDateTime;

import static ru.practicum.ewmService.util.Util.YYYY_MM_DD_HH_MM_SS;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewEventDto {

    @NotNull
    @Length(min = 20, max = 2000)
    private String annotation;

    @NotNull
    private Long category;

    @NotNull
    @Length(min = 20, max = 7000)
    private String description;

    @NotNull
    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = YYYY_MM_DD_HH_MM_SS)
    private LocalDateTime eventDate;

    @NotNull
    private LocationDto location;

    private Boolean paid;

    private Long participantLimit;

    private Boolean requestModeration;

    @NotNull
    @Length(min = 3, max = 120)
    private String title;

}
package ru.practicum.ewmService.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

import java.time.LocalDateTime;


@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class CommentDto {

    private Long id;

    private String authorName;

    @NotBlank
    @Length(max = 1000)
    private String text;

    @JsonFormat
            (shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime created;
}

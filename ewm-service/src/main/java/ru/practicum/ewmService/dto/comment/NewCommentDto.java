package ru.practicum.ewmService.dto.comment;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@Builder
@RequiredArgsConstructor
@AllArgsConstructor
public class NewCommentDto {

    private Long id;

    @NotBlank
    @Length(max = 1000)
    private String text;
}

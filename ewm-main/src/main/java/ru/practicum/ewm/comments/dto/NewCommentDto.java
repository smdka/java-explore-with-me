package ru.practicum.ewm.comments.dto;

import lombok.Value;

import javax.validation.constraints.NotBlank;

@Value
public class NewCommentDto {
    Long id;

    @NotBlank
    String text;
}

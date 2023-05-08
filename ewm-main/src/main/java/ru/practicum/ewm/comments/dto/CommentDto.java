package ru.practicum.ewm.comments.dto;

import lombok.Value;
import ru.practicum.ewm.events.dto.EventDto;
import ru.practicum.ewm.users.dto.UserShortDto;

import java.time.LocalDateTime;

@Value
public class CommentDto {
    Long id;
    UserShortDto author;
    EventDto event;
    String text;
    LocalDateTime createdOn;
}

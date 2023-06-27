package ru.practicum.ewm.comments.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.hateoas.RepresentationModel;
import ru.practicum.ewm.events.dto.EventDto;
import ru.practicum.ewm.users.dto.UserShortDto;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Value
public class CommentDto extends RepresentationModel<CommentDto> {
    Long id;
    UserShortDto author;
    EventDto event;
    String text;
    LocalDateTime createdOn;
}

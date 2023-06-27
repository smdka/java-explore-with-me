package ru.practicum.ewm.events.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.hateoas.RepresentationModel;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.locations.dto.NewLocationDto;
import ru.practicum.ewm.users.dto.UserShortDto;

import java.time.LocalDateTime;

@EqualsAndHashCode(callSuper = true)
@Value
public class EventDto extends RepresentationModel<EventDto> {
    Long id;
    String annotation;
    CategoryDto category;
    Integer confirmedRequests;
    LocalDateTime createdOn;
    String description;
    LocalDateTime eventDate;
    UserShortDto initiator;
    NewLocationDto location;
    Boolean paid;
    Integer participantLimit;
    LocalDateTime publishedOn;
    Boolean requestModeration;
    State state;
    String title;
    Long views;
}

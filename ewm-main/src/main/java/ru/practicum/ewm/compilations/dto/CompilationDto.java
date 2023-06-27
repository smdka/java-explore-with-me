package ru.practicum.ewm.compilations.dto;

import lombok.EqualsAndHashCode;
import lombok.Value;
import org.springframework.hateoas.RepresentationModel;
import ru.practicum.ewm.events.dto.EventDto;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Value
public class CompilationDto extends RepresentationModel<CompilationDto> {
    Long id;
    Boolean pinned;
    String title;
    List<EventDto> events;
}

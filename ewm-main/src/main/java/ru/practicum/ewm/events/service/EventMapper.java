package ru.practicum.ewm.events.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.categories.dto.CategoryDto;
import ru.practicum.ewm.categories.model.Category;
import ru.practicum.ewm.events.dto.EventDto;
import ru.practicum.ewm.events.dto.NewEventDto;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.locations.dto.LocationDto;
import ru.practicum.ewm.users.dto.UserDto;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;


@Mapper
public interface EventMapper {
    EventMapper MAP = Mappers.getMapper(EventMapper.class);

    @Mapping(target = "category", expression = "java(categoryDtoToCategory(categoryDto))")
    @Mapping(target = "confirmedRequests", ignore = true)
    @Mapping(target = "createdOn", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "initiator", source = "userDto")
    @Mapping(target = "publishedOn", ignore = true)
    @Mapping(target = "state", source = "newEventDto.stateAction")
    Event toModel(NewEventDto newEventDto, LocationDto locationDto, UserDto userDto, CategoryDto categoryDto);

    default Category categoryDtoToCategory(CategoryDto categoryDto) {
        return new Category(categoryDto.getId(), categoryDto.getName());
    }

    @Mapping(target = "views", constant = "0L")
    EventDto toDto(Event event);

    @Mapping(target = "views", source = "hits")
    EventDto toDto(Event event, Long hits);

    default List<EventDto> toDto(Collection<Event> events, Map<String, Long> eventViews) {
        return events.stream().map(event -> {
            Long hits = eventViews.get(String.format("/events/%s", event.getId()));

            return toDto(event, hits != null ? hits : 0);
        }).collect(toList());
    }

    List<EventDto> toDto(Collection<Event> events);
}

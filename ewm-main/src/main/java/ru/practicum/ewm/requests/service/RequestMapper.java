package ru.practicum.ewm.requests.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.events.dto.State;
import ru.practicum.ewm.requests.dto.RequestDto;
import ru.practicum.ewm.requests.model.Request;

import java.util.Collection;
import java.util.List;

@Mapper
public interface RequestMapper {
    RequestMapper MAP = Mappers.getMapper(RequestMapper.class);

    @Mapping(target = "requester", source = "requesterId")
    @Mapping(target = "event", source = "eventId")
    RequestDto toDto(Request request);

    List<RequestDto> toDto(Collection<Request> requests);

    @Mapping(target = "status", source = "state")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "created", ignore = true)
    Request toModel(Long eventId, Long requesterId, State state);
}
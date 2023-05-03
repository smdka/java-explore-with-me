package ru.practicum.ewm.model;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.practicum.ewm.dto.EndpointHitDto;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HitMapper {

    public static EndpointHit toModel(EndpointHitDto endpointHitDto) {
        EndpointHit endpointHit = new EndpointHit();
        endpointHit.setIp(endpointHitDto.getIp());
        endpointHit.setApp(endpointHitDto.getApp());
        endpointHit.setUri(endpointHitDto.getUri());
        endpointHit.setTimestamp(endpointHitDto.getTimestamp());
        return endpointHit;
    }


    public static EndpointHitDto toDto(EndpointHit endpointHit) {
        return new EndpointHitDto(endpointHit.getApp(),
                endpointHit.getUri(),
                endpointHit.getIp(),
                endpointHit.getTimestamp());
    }
}

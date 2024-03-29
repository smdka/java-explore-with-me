package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;
import ru.practicum.ewm.model.EndpointHitMapper;
import ru.practicum.ewm.repository.EndpointHitRepository;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class EndpointHitServiceImpl implements EndpointHitService {
    private final EndpointHitRepository repository;

    @Override
    @Transactional
    public void add(EndpointHitDto endpointHitDto) {
        repository.save(EndpointHitMapper.MAP.toModel(endpointHitDto));
    }

    @Override
    public Collection<ViewStatsDto> get(LocalDateTime start, LocalDateTime end, List<String> uris, boolean unique) {
        if (unique) {
            return isCollectionEmptyOrNull(uris)
                    ? repository.findByDateUniqueIp(start, end)
                    : repository.findByDateAndUrisUniqueIp(start, end, uris);
        }
        return isCollectionEmptyOrNull(uris)
                ? repository.findByDate(start, end)
                : repository.findByDateAndUris(start, end, uris);
    }

    private <T> boolean isCollectionEmptyOrNull(Collection<T> collection) {
        return collection == null || collection.isEmpty();
    }
}

package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.EndpointHitDto;
import ru.practicum.ewm.ViewStats;
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
    public EndpointHitDto add(EndpointHitDto endpointHitDto) {
        return EndpointHitMapper.toDto(repository.save(EndpointHitMapper.toModel(endpointHitDto)));
    }

    @Override
    public Collection<ViewStats> get(LocalDateTime start, LocalDateTime end, List<String> uris, Boolean unique) {
        if (unique) {
            return isEmptyOrNull(uris)
                    ? repository.findByDateUniqueIp(start, end)
                    : repository.findByDateAndUrisUniqueIp(start, end, uris);
        }
        return isEmptyOrNull(uris)
                ? repository.findByDate(start, end)
                : repository.findByDateAndUris(start, end, uris);
    }

    private <T> boolean isEmptyOrNull(List<T> list) {
        return list == null || list.isEmpty();
    }
}

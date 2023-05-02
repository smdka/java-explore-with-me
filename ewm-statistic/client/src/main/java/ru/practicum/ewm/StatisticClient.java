package ru.practicum.ewm;

import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.ewm.dto.EndpointHitDto;
import ru.practicum.ewm.dto.ViewStatsDto;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class StatisticClient extends BaseClient {
    @Autowired
    public StatisticClient(@Value("${STATS_SERVER_URL}") String serverUrl, RestTemplateBuilder builder) {
        super(builder
                .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl))
                .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                .build());
    }

    public void createHit(EndpointHitDto endpointHitDto) {
        log.info(String.format("Statistic client createHit: endpointHitDto=%s", endpointHitDto));
        Gson gson = new Gson();
        ResponseEntity<Object> objectResponseEntity = post("/hit", endpointHitDto);
        String json = gson.toJson(objectResponseEntity.getBody());

        gson.fromJson(json, ViewStatsDto.class);
    }

    public Collection<ViewStatsDto> getStats(String start, String end, List<String> uris, Boolean unique) {
        log.info(String.format("Statistic client getStats: start=%s, end=%s, uris=%s, unique=%s", start, end, uris, unique));
        Gson gson = new Gson();
        Map<String, Object> parameters = Map.of(
                "uris", String.join(",", uris),
                "unique", unique,
                "start", start,
                "end", end
        );
        ResponseEntity<Object> objectResponseEntity =
                get("/stats?start={start}&end={end}&uris={uris}&unique={unique}", parameters);
        String json = gson.toJson(objectResponseEntity.getBody());
        ViewStatsDto[] viewStatDtoArray = gson.fromJson(json, ViewStatsDto[].class);

        return Arrays.asList(viewStatDtoArray);
    }
}
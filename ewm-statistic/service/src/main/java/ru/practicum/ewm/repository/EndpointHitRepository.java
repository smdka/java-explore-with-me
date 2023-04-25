package ru.practicum.ewm.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.ViewStats;
import ru.practicum.ewm.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

public interface EndpointHitRepository extends JpaRepository<EndpointHit, Long> {

    @Query("SELECT new ru.practicum.ewm.ViewStats(eh.uri, eh.app, COUNT (eh.ip)) " +
           "FROM EndpointHit eh " +
           "WHERE eh.timestamp BETWEEN :start AND :end " +
           "GROUP BY eh.app, eh.uri " +
           "ORDER BY COUNT(eh.ip) DESC ")
    Collection<ViewStats> findByDate(@Param("start") LocalDateTime start, @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.ewm.ViewStats(eh.uri, eh.app, COUNT (eh.ip)) " +
           "FROM EndpointHit eh " +
           "WHERE eh.timestamp BETWEEN :start AND :end " +
           "AND eh.uri IN :uris " +
           "GROUP BY eh.app, eh.uri " +
           "ORDER BY COUNT (eh.ip) DESC ")
    Collection<ViewStats> findByDateAndUris(@Param("start") LocalDateTime start,
                                            @Param("end") LocalDateTime end,
                                            @Param("uris") List<String> uris);

    @Query("SELECT new ru.practicum.ewm.ViewStats(eh.uri, eh.app, COUNT (DISTINCT eh.ip)) " +
           "FROM EndpointHit eh " +
           "WHERE eh.timestamp BETWEEN :start AND :end " +
           "GROUP BY eh.app, eh.uri " +
           "ORDER BY COUNT (eh.ip) DESC ")
    Collection<ViewStats> findByDateUniqueIp(@Param("start") LocalDateTime start,
                                             @Param("end") LocalDateTime end);

    @Query("SELECT new ru.practicum.ewm.ViewStats(eh.uri, eh.app, COUNT (DISTINCT eh.ip)) " +
           "FROM EndpointHit eh " +
           "WHERE eh.timestamp BETWEEN :start AND :end " +
           "AND eh.uri IN :uris " +
           "GROUP BY eh.app, eh.uri " +
           "ORDER BY COUNT (eh.ip) DESC ")
    Collection<ViewStats> findByDateAndUrisUniqueIp(@Param("start") LocalDateTime start,
                                                    @Param("end") LocalDateTime end,
                                                    @Param("uris") List<String> uris);
}

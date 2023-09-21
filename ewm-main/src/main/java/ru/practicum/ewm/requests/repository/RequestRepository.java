package ru.practicum.ewm.requests.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.ewm.requests.model.Request;
import ru.practicum.ewm.requests.model.RequestStat;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {

    Collection<Request> findAllByRequesterId(Long requesterId);

    Collection<Request> findAllByEventId(Long eventId);

    Optional<Request> findByRequesterIdAndId(Long requesterId, Long id);

    Optional<Request> findByRequesterIdAndEventId(Long requesterId, Long eventId);

    @Query("SELECT new ru.practicum.ewm.requests.model.RequestStat(r.eventId, COUNT(r.eventId)) " +
           "FROM Request r " +
           "WHERE r.eventId IN :ids " +
           "GROUP BY r.eventId")
    Collection<RequestStat> getRequestsStats(@Param("ids") List<Long> ids);

    long countByEventId(Long eventId);
}
package ru.practicum.ewm.requests.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Getter
@Setter
@ToString
public class RequestStat {
    @Id
    private Long eventId;

    private Long requests;

    public RequestStat(Long eventId, Long requests) {
        this.eventId = eventId;
        this.requests = requests;
    }

    public RequestStat() {
    }
}
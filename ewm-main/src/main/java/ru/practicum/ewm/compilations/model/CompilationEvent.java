package ru.practicum.ewm.compilations.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Entity
@Table(name = "compilation_events")
@IdClass(CompilationEventId.class)
@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class CompilationEvent {
    @Id
    @Column(name = "compilation_id")
    private Long compilationId;

    @Id
    @Column(name = "event_id")
    private Long eventId;
}

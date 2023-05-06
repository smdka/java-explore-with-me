package ru.practicum.ewm.comments.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.users.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comments")
@NamedEntityGraph(name = "comment_entity_graph", attributeNodes = {
        @NamedAttributeNode("author"),
        @NamedAttributeNode(value = "event", subgraph = "comment_entity_subgraph")
}, subgraphs = {
        @NamedSubgraph(name = "comment_entity_subgraph", attributeNodes = {
                @NamedAttributeNode(value = "category"),
                @NamedAttributeNode(value = "initiator")
        }),
})

public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id")
    private Event event;

    private String text;

    @Column(name = "created_on")
    @CreationTimestamp
    private LocalDateTime createdOn;
}

package ru.practicum.ewm.comments.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.ewm.comments.model.Comment;

import java.util.List;
import java.util.Optional;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    @EntityGraph("comment_entity_graph")
    List<Comment> findAllByAuthorId(Long userId, PageRequest page);

    @EntityGraph("comment_entity_graph")
    List<Comment> findAllByEventId(Long eventId, PageRequest page);

    @Override
    @EntityGraph("comment_entity_graph")
    Optional<Comment> findById(Long id);
}

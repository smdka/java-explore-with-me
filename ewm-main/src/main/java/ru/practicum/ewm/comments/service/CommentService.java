package ru.practicum.ewm.comments.service;

import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;

import java.util.Collection;

public interface CommentService {
    CommentDto add(Long userId, Long eventId, NewCommentDto commentDto);

    CommentDto update(Long userId, Long commentId, NewCommentDto commentDto);

    CommentDto get(Long userId, Long commentId);

    Collection<CommentDto> getAllByUserId(Long userId, Integer from, Integer size);

    Collection<CommentDto> getAllByEventId(Long eventId, Integer from, Integer size);

    void delete(Long commentId);
}

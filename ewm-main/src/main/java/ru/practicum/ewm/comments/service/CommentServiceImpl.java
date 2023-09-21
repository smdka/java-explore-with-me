package ru.practicum.ewm.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.comments.repository.CommentRepository;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.events.service.EventServiceImpl;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.users.repository.UserRepository;
import ru.practicum.ewm.users.service.UserServiceImpl;

import java.util.Collection;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {
    private static final String COMMENT_NOT_FOUND_MSG = "Комментарий с id=%d не найден";
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final CommentRepository commentRepository;

    @Override
    @Transactional
    public CommentDto add(Long userId, Long eventId, NewCommentDto commentDto) {
        return userRepository.findById(userId)
                .map(user -> eventRepository.findById(eventId)
                        .map(event -> CommentMapper.MAP.toDto(commentRepository.save(CommentMapper.MAP.toModel(commentDto, user, event))))
                        .orElseThrow(() -> new NotFoundException(String.format(EventServiceImpl.EVENT_NOT_FOUND_MSG, eventId))))
                .orElseThrow(() -> new NotFoundException(String.format(UserServiceImpl.USER_NOT_FOUND_MSG, userId)));
    }

    @Override
    @Transactional
    public CommentDto update(Long userId, Long commentId, NewCommentDto commentDto) {
        return commentRepository.findById(commentId)
                .filter(comment -> comment.getAuthor().getId().equals(userId))
                .map(comment -> updateComment(commentDto, comment))
                .orElseThrow(() -> new NotFoundException(String.format(COMMENT_NOT_FOUND_MSG, commentId)));
    }

    private CommentDto updateComment(NewCommentDto commentDto, Comment comment) {
        comment.setText(commentDto.getText());
        return CommentMapper.MAP.toDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto get(Long userId, Long commentId) {
        return commentRepository.findById(commentId)
                .filter(comment -> comment.getAuthor().getId().equals(userId))
                .map(CommentMapper.MAP::toDto)
                .orElseThrow(() -> new NotFoundException(String.format(COMMENT_NOT_FOUND_MSG, commentId)));
    }

    @Override
    public Collection<CommentDto> getAllByUserId(Long userId, Integer from, Integer size) {
        return userRepository.findById(userId)
                .map(user -> CommentMapper.MAP.toDto(commentRepository.findAllByAuthorId(userId, PageRequest.of(from, size))))
                .orElseThrow(() -> new NotFoundException(String.format(UserServiceImpl.USER_NOT_FOUND_MSG, userId)));
    }

    @Override
    public Collection<CommentDto> getAllByEventId(Long eventId, Integer from, Integer size) {
        return eventRepository.findById(eventId)
                .map(event -> CommentMapper.MAP.toDto(commentRepository.findAllByEventId(eventId, PageRequest.of(from, size))))
                .orElseThrow(() -> new NotFoundException(String.format(EventServiceImpl.EVENT_NOT_FOUND_MSG, eventId)));
    }

    @Override
    public void delete(Long commentId) {
        commentRepository.findById(commentId)
                .ifPresentOrElse(comment -> commentRepository.deleteById(commentId), () -> {
                    throw new NotFoundException(String.format(COMMENT_NOT_FOUND_MSG, commentId));
                });
    }
}

package ru.practicum.ewm.comments.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.comments.repository.CommentRepository;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.events.repository.EventRepository;
import ru.practicum.ewm.events.service.EventServiceImpl;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.exceptions.OperationException;
import ru.practicum.ewm.users.model.User;
import ru.practicum.ewm.users.repository.UserRepository;
import ru.practicum.ewm.users.service.UserServiceImpl;

import java.util.Collection;
import java.util.Optional;

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
        User author = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(UserServiceImpl.USER_NOT_FOUND_MSG, userId)));

        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(EventServiceImpl.EVENT_NOT_FOUND_MSG, eventId)));

        return CommentMapper.MAP.toDto(commentRepository.save(CommentMapper.MAP.toModel(commentDto, author, event)));
    }

    @Override
    @Transactional
    public CommentDto update(Long userId, Long commentId, NewCommentDto commentDto) {
        Comment comment = commentRepository.findById(commentId)
                        .orElseThrow(() -> new NotFoundException(String.format(COMMENT_NOT_FOUND_MSG, commentId)));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new OperationException("Пользователь не может редактировать чужой комментарий");
        }

        comment.setText(commentDto.getText());
        return CommentMapper.MAP.toDto(commentRepository.save(comment));
    }

    @Override
    public CommentDto get(Long userId, Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                        .orElseThrow(() -> new NotFoundException(String.format(COMMENT_NOT_FOUND_MSG, commentId)));

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new OperationException("Пользователь может получить только свой комментарий");
        }

        return CommentMapper.MAP.toDto(comment);
    }

    @Override
    public Collection<CommentDto> getAllByUserId(Long userId, Integer from, Integer size) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(UserServiceImpl.USER_NOT_FOUND_MSG, userId));
        }

        PageRequest page = PageRequest.of(from, size);

        return CommentMapper.MAP.toDto(commentRepository.findAllByAuthorId(userId, page));
    }

    @Override
    public Collection<CommentDto> getAllByEventId(Long eventId, Integer from, Integer size) {
        if (!eventRepository.existsById(eventId)) {
            throw new NotFoundException(String.format(UserServiceImpl.USER_NOT_FOUND_MSG, eventId));
        }

        PageRequest page = PageRequest.of(from, size);

        return CommentMapper.MAP.toDto(commentRepository.findAllByEventId(eventId, page));
    }

    @Override
    public void delete(Long commentId) {
        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException(String.format(COMMENT_NOT_FOUND_MSG, commentId));
        }
        commentRepository.deleteById(commentId);
    }
}

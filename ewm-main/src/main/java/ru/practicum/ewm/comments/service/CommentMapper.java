package ru.practicum.ewm.comments.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.comments.dto.CommentDto;
import ru.practicum.ewm.comments.dto.NewCommentDto;
import ru.practicum.ewm.comments.model.Comment;
import ru.practicum.ewm.events.model.Event;
import ru.practicum.ewm.users.model.User;

import java.util.Collection;
import java.util.List;

@Mapper
public interface CommentMapper {
    CommentMapper MAP = Mappers.getMapper(CommentMapper.class);

    @Mapping(target = "id", ignore = true)
    CommentDto toDto(Comment comment);

    List<CommentDto> toDto(Collection<Comment> comments);

    @Mapping(target = "id", source = "commentDto.id")
    Comment toModel(NewCommentDto commentDto, User author, Event event);
}

package ru.practicum.ewm.users.service;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import ru.practicum.ewm.users.dto.NewUserDto;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.model.User;

import java.util.Collection;
import java.util.List;


@Mapper
public interface UserMapper {
    UserMapper MAP = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", ignore = true)
    User toModel(NewUserDto newUserDto);

    UserDto toDto(User user);

    List<UserDto> toDto(Collection<User> users);
}
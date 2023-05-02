package ru.practicum.ewm.users.service;

import lombok.experimental.UtilityClass;
import ru.practicum.ewm.users.dto.NewUserDto;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.model.User;

import java.util.Collection;
import java.util.List;

import static java.util.stream.Collectors.toList;


@UtilityClass
public class UserMapper {
    public static User toModel(NewUserDto newUserDto) {
        User user = new User();

        user.setName(newUserDto.getName());
        user.setEmail(newUserDto.getEmail());

        return user;
    }

    public static UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getName(),
                user.getEmail());
    }

    public static List<UserDto> toDto(Collection<User> users) {
        return users
                .stream()
                .map(UserMapper::toDto)
                .collect(toList());
    }
}
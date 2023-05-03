package ru.practicum.ewm.users.service;

import ru.practicum.ewm.users.dto.NewUserDto;
import ru.practicum.ewm.users.dto.UserDto;

import java.util.Collection;
import java.util.List;

public interface UserService {
    UserDto add(NewUserDto newUserDto);

    Collection<UserDto> getAll(List<Long> ids, Integer from, Integer size);

    void delete(Long userId);
}

package ru.practicum.ewm.users.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.ewm.exceptions.NotFoundException;
import ru.practicum.ewm.users.dto.NewUserDto;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.model.User;
import ru.practicum.ewm.users.repository.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;

    private static final String USER_NOT_FOUND_MSG = "Пользователь с id=%s не найден";

    @Override
    @Transactional
    public UserDto add(NewUserDto newUserDto) {
        User user = UserMapper.toModel(newUserDto);

        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public Collection<UserDto> getAll(List<Long> ids, Integer from, Integer size) {
        Pageable pageable = PageRequest.of(from, size);

        return UserMapper.toDto(userRepository.findAllUsersByIds(ids, pageable));
    }

    @Override
    @Transactional
    public void deleteById(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new NotFoundException(String.format(USER_NOT_FOUND_MSG, userId));
        }
        userRepository.deleteById(userId);
    }
}

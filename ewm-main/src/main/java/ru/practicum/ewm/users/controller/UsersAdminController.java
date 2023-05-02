package ru.practicum.ewm.users.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.ewm.users.dto.NewUserDto;
import ru.practicum.ewm.users.dto.UserDto;
import ru.practicum.ewm.users.service.UserService;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("admin")
@RequiredArgsConstructor
public class UsersAdminController {

    private final UserService userService;

    @PostMapping("/users")
    @ResponseStatus(HttpStatus.CREATED)
    public UserDto create(@RequestBody @Valid NewUserDto newUserDto) {
        log.info("POST /admin/users");
        log.info("Request body: {}", newUserDto);
        return userService.add(newUserDto);
    }

    @GetMapping("/users")
    public Collection<UserDto> getAll(@RequestParam(required = false) List<Long> ids,
                                      @RequestParam(defaultValue = "0") Integer from,
                                      @RequestParam(defaultValue = "10") Integer size) {
        log.info("GET /admin/users?ids={}&from={}&size={}", ids, from, size);
        return userService.getAll(ids, from, size);
    }

    @DeleteMapping("/users/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteById(@PathVariable Long userId) {
        log.info("DELETE /admin/users/{}", userId);
        userService.deleteById(userId);
    }
}
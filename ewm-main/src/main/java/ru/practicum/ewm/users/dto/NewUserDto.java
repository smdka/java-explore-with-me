package ru.practicum.ewm.users.dto;

import lombok.Value;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Value
public class NewUserDto {
    @NotBlank
    String name;

    @Email
    @NotBlank
    String email;
}
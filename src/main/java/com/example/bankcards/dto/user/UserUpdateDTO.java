package com.example.bankcards.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
public class UserUpdateDTO {

    @NotBlank(message = "Имя пользователя не должно быть пустым")
    JsonNullable<String> username;

    @NotBlank(message = "Пароль не должен быть пустым")
    @Size(min = 3, message = "Пароль должен содержать не менее трех символов")
    JsonNullable<String> password;
}

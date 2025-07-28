package com.example.bankcards.dto.user;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.openapitools.jackson.nullable.JsonNullable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDTO {

    @NotBlank(message = "Имя пользователя не должно быть пустым")
    @Schema(example = "newName", implementation = String.class, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    JsonNullable<String> username = JsonNullable.undefined();

    @NotBlank(message = "Пароль не должен быть пустым")
    @Size(min = 3, message = "Пароль должен содержать не менее трех символов")
    @Schema(example = "newPassword", implementation = String.class, requiredMode = Schema.RequiredMode.NOT_REQUIRED)
    JsonNullable<String> password = JsonNullable.undefined();
}

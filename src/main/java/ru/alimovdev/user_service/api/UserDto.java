package ru.alimovdev.user_service.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import org.springframework.hateoas.RepresentationModel;

import java.sql.Timestamp;

//@Schema - аннотация добавляет описание к классу или полю в документации Swagger/OpenAPI
@Schema(description = "DTO пользователя")
@lombok.Setter
@lombok.Getter
public class UserDto extends RepresentationModel<UserDto> {
/*
Наследование RepresentationModel позволяет UserDto хранить ссылки (links). После этого
в контроллер вместо возврата простого DTO будет приходить DTO, обогащённый ссылками.
*/

    @Schema(description = "Уникальный идентификатор пользователя", example = "1")
    private Long id;

    @Schema(description = "Точные дата и время создания нового пользователя", example = "2026-08-07T10:15:30.000+0000")
    private Timestamp created_at;

    @Schema(description = "Имя пользователя", example = "Dmitriy")
    @NotBlank(message = "Имя не может быть пустым")
    @Size(max = 15, message = "Имя не должно превышать 15 символов")
    private String name;

    @Schema(description = "Адрес электронной почты пользователя", example = "dmitry@test.com")
    @NotBlank(message = "Email не может быть пустым")
    @Email(message = "Некорректный формат email")
    @Size(max = 25, message = "Email не должен превышать 50 символов")
    private String email;

    @Schema(description = "Возраст пользователя", example = "33", minimum = "18", maximum = "100")
    @NotNull(message = "Возраст обязателен")
    @Min(value = 18, message = "Возраст должен быть не менее 18")
    @Max(value = 130, message = "Возраст должен быть не более 100")
    private Integer age;

    public UserDto() {}

}


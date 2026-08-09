package ru.alimovdev.user_service.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.alimovdev.user_service.api.UserDto;
import ru.alimovdev.user_service.service.UserService;
import org.springframework.hateoas.CollectionModel;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

/**
 * linkTo(methodOn(...)).withSelfRel() – строит URL, соответствующий вызову указанного метода контроллера.
 * Это типобезопасный способ создания ссылок, который защищает от опечаток и изменений URL-адресов.
 * HATEOAS-ссылки – добавляются в DTO с помощью метода add(...), унаследованного от RepresentationModel.
 * Эти ссылки будут видны клиенту в поле _links ответа в формате HAL.
 * Пример JSON для при запросе User клиентом:
 * {
 * "_links": {
 * "self": {
 * "href": "http://localhost:8080/api/users_hateoas/35"
 * },
 * "all-users": {
 * "href": "http://localhost:8080/api/users_hateoas"
 * }
 * },
 * "age": 31,
 * "created_at": "2026-08-03T07:36:36.298Z",
 * "email": "al.dimitry@yandex.ru",
 * "id": 35,
 * "name": "Dmitriy"
 * }
 */

@RestController
@RequestMapping("/api/users_hateoas")
@Tag(name = "Users-HATEOAS", description = "API для управления пользователями с использованием гипермедиа ссылок")
// Swagger получает заголовок с описанием класса
@Slf4j
public class UserHateoasController {

    private final UserService userService;

    public UserHateoasController(UserService userService) {
        this.userService = userService;
    }


    @Operation(summary = "Получение пользователя по id", // Swagger получает заглавие с функционалом метода
            description = "Возвращает информацию о пользователе по его идентификатору, а также ссылку на список всех пользователей")
    // Swagger получает описание функционала
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Пользователь найден",
                    // content - указание для Swagger, что при успешном ответе (200) тело будет содержать UserDto
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")  // кейс, когда пользователь не найден
    })
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        /* linkTo(methodOn(...)).withSelfRel() – строит URL, соответствующий вызову указанного метода контроллера.
        Это типобезопасный способ создания ссылок, который защищает от опечаток и изменений URL-адресов.
        HATEOAS-ссылки – добавляются в DTO с помощью метода add(...), унаследованного от RepresentationModel. Эти
        ссылки будут видны клиенту в поле _links ответа в формате HAL.     */
        return userService.getUser(id)
                .map(userDto -> {
                    // Создаётся ссылка на самого себя (self)
                    Link selfLink = linkTo(methodOn(UserHateoasController.class).getUser(id)).withSelfRel();
                    // Создаётся ссылка на список всех пользователей
                    Link allUsersLink = linkTo(methodOn(UserHateoasController.class).getAllUsers()).withRel("all-users");
                    // Через метод add() ссылки добавляются в DTO
                    userDto.add(selfLink, allUsersLink);
                    return ResponseEntity.ok(userDto); // в ResponseEntity уходит userDto со ссылками для возможности перехода по ним
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(summary = "Редактирование данных пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Данные обновлены, + ссылка на список всех пользователей",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Невалидные данные")
    })
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody @Valid UserDto userDto) {
        return userService.updateUser(id, userDto)
                .map(updated -> {
                    Link selfLink = linkTo(methodOn(UserHateoasController.class).getUser(id)).withSelfRel();
                    Link allUsersLink = linkTo(methodOn(UserHateoasController.class).getAllUsers()).withRel("all-users");
                    updated.add(selfLink, allUsersLink);
                    return ResponseEntity.ok(updated);
                })
                .orElse(ResponseEntity.notFound().build());
    }


    @Operation(summary = "Получение списка всех пользователей")
    @ApiResponse(responseCode = "200", description = "Список пользователей",
            content = @Content(schema = @Schema(implementation = UserDto.class)))
    @GetMapping
    public CollectionModel<UserDto> getAllUsers() {
        List<UserDto> users = userService.getAllUsers();
        // Создаётся ссылка self для всего списка
        Link selfLink = linkTo(methodOn(UserHateoasController.class).getAllUsers()).withSelfRel();
        return CollectionModel.of(users, selfLink);
    }


    @Operation(summary = "Добавление нового пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Пользователь создан",
                    content = @Content(schema = @Schema(implementation = UserDto.class))),
            @ApiResponse(responseCode = "400", description = "Невалидные данные")
    })
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto userDto) {
        UserDto saved = userService.createUser(userDto);
        Link selfLink = linkTo(methodOn(UserHateoasController.class).getUser(saved.getId())).withSelfRel();
        saved.add(selfLink);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }


    // В отличие от методов GET и POST, deleteUser не добавляет HATEOAS-ссылок,
    // т.к. после удаления ресурса дальнейшее взаимодействие с ним не предусматривается
    @Operation(summary = "Удалить пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Пользователь удалён"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (userService.deleteUser(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

}

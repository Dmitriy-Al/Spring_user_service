package ru.alimovdev.user_service;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.alimovdev.user_service.api.UserDto;
import ru.alimovdev.user_service.controller.UserController;
import ru.alimovdev.user_service.service.UserService;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private UserService userService;

    @Test
    void getUser_shouldReturnUser_whenExists() throws Exception {
        Long userId = 1L;
        UserDto dto = new UserDto();
        dto.setId(userId);
        dto.setName("Dima");
        dto.setEmail("dima@test.com");
        dto.setAge(30);
        dto.setCreated_at(new Timestamp(System.currentTimeMillis()));

        when(userService.getUser(userId)).thenReturn(Optional.of(dto));

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Dima"));
    }

    @Test
    void getUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        Long userId = 99L;
        when(userService.getUser(userId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void getAllUsers_shouldReturnList() throws Exception {
        UserDto dto1 = new UserDto();
        dto1.setId(1L);
        dto1.setName("Dima");
        UserDto dto2 = new UserDto();
        dto2.setId(2L);
        dto2.setName("Alex");
        List<UserDto> dtos = List.of(dto1, dto2);

        when(userService.getAllUsers()).thenReturn(dtos);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Dima"))
                .andExpect(jsonPath("$[1].name").value("Alex"));
    }

    @Test
    void createUser_shouldReturnCreated() throws Exception {
        UserDto inputDto = new UserDto();
        inputDto.setName("Dima");
        inputDto.setEmail("dima@test.com");
        inputDto.setAge(25);

        UserDto savedDto = new UserDto();
        savedDto.setId(1L);
        savedDto.setName("Dima");
        savedDto.setEmail("dima@test.com");
        savedDto.setAge(25);
        savedDto.setCreated_at(new Timestamp(System.currentTimeMillis()));

        when(userService.createUser(any(UserDto.class))).thenReturn(savedDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.name").value("Dima"));
    }

    @Test
    void createUser_shouldReturnBadRequest_whenValidationFails() throws Exception {
        UserDto invalidDto = new UserDto();
        invalidDto.setName(""); // пустое имя
        invalidDto.setEmail("invalid"); // невалидный email
        invalidDto.setAge(15); // возраст < 18

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateUser_shouldReturnUpdatedUser_whenExists() throws Exception {
        Long userId = 1L;
        UserDto inputDto = new UserDto();
        inputDto.setName("Updated");
        inputDto.setEmail("updated@test.com");
        inputDto.setAge(30);

        UserDto updatedDto = new UserDto();
        updatedDto.setId(userId);
        updatedDto.setName("Updated");
        updatedDto.setEmail("updated@test.com");
        updatedDto.setAge(30);
        updatedDto.setCreated_at(new Timestamp(System.currentTimeMillis()));

        when(userService.updateUser(eq(userId), any(UserDto.class))).thenReturn(Optional.of(updatedDto));

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Updated"));
    }

    @Test
    void updateUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        Long userId = 99L;
        UserDto inputDto = new UserDto();
        inputDto.setName("Updated");
        inputDto.setEmail("updated@test.com");
        inputDto.setAge(30);

        when(userService.updateUser(eq(userId), any(UserDto.class))).thenReturn(Optional.empty());

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteUser_shouldReturnNoContent_whenExists() throws Exception {
        Long userId = 1L;
        when(userService.deleteUser(userId)).thenReturn(true);

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteUser_shouldReturnNotFound_whenDoesNotExist() throws Exception {
        Long userId = 99L;
        when(userService.deleteUser(userId)).thenReturn(false);

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }
}
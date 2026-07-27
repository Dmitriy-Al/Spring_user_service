package ru.alimovdev.user_service.controller;
// import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
// import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean; // <- НОВЫЙ ИМПОРТ
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import ru.alimovdev.user_service.api.UserDto;
import ru.alimovdev.user_service.api.UserMapper;
import ru.alimovdev.user_service.model.User;
import ru.alimovdev.user_service.repository.UserRepository;

import java.sql.Timestamp;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserRepository userRepository;

    @MockitoBean
    private UserMapper userMapper;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getUser_shouldReturnUser_whenExists() throws Exception {
        Long userId = 1L;
        User user = new User();
        user.setId(userId);
        user.setName("Fedya");
        user.setEmail("fed@example.com");
        user.setAge(30);
        user.setCreated_at(new Timestamp(System.currentTimeMillis()));

        UserDto dto = new UserDto();
        dto.setId(userId);
        dto.setName("Fedya");
        dto.setEmail("fed@example.com");
        dto.setAge(30);
        dto.setCreated_at(user.getCreated_at());

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toDto(user)).thenReturn(dto);

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("Fedya"))
                .andExpect(jsonPath("$.email").value("fed@example.com"))
                .andExpect(jsonPath("$.age").value(30));
    }

    @Test
    void getUser_shouldReturnNotFound_whenUserNotExists() throws Exception {
        Long userId = 99L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/users/{id}", userId))
                .andExpect(status().isNotFound());
    }

    @Test
    void createUser_shouldReturnCreated() throws Exception {
        UserDto inputDto = new UserDto();
        inputDto.setName("Alice");
        inputDto.setEmail("alice@example.com");
        inputDto.setAge(25);

        User userToSave = new User();
        userToSave.setName("Alice");
        userToSave.setEmail("alice@example.com");
        userToSave.setAge(25);

        User savedUser = new User();
        savedUser.setId(2L);
        savedUser.setName("Alice");
        savedUser.setEmail("alice@example.com");
        savedUser.setAge(25);
        savedUser.setCreated_at(new Timestamp(System.currentTimeMillis()));

        UserDto responseDto = new UserDto();
        responseDto.setId(2L);
        responseDto.setName("Alice");
        responseDto.setEmail("alice@example.com");
        responseDto.setAge(25);
        responseDto.setCreated_at(savedUser.getCreated_at());

        when(userMapper.toNewEntity(any(UserDto.class))).thenReturn(userToSave);
        when(userRepository.save(any(User.class))).thenReturn(savedUser);
        when(userMapper.toDto(savedUser)).thenReturn(responseDto);

        mockMvc.perform(post("/api/users")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inputDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(2L))
                .andExpect(jsonPath("$.name").value("Alice"));
    }


}
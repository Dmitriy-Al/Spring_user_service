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
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
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

    @Test
    void getAllUsers_shouldReturnListOfUsers() throws Exception {
        User user1 = new User();
        user1.setId(1L);
        user1.setName("Fedya");
        user1.setEmail("fed@example.com");
        user1.setAge(30);
        user1.setCreated_at(new Timestamp(System.currentTimeMillis()));

        User user2 = new User();
        user2.setId(2L);
        user2.setName("Alice");
        user2.setEmail("alice@example.com");
        user2.setAge(25);
        user2.setCreated_at(new Timestamp(System.currentTimeMillis()));

        List<User> users = Arrays.asList(user1, user2);

        UserDto dto1 = new UserDto();
        dto1.setId(1L);
        dto1.setName("Fedya");
        dto1.setEmail("fed@example.com");
        dto1.setAge(30);
        dto1.setCreated_at(user1.getCreated_at());

        UserDto dto2 = new UserDto();
        dto2.setId(2L);
        dto2.setName("Alice");
        dto2.setEmail("alice@example.com");
        dto2.setAge(25);
        dto2.setCreated_at(user2.getCreated_at());

        when(userRepository.findAll()).thenReturn(users);
        when(userMapper.toDto(user1)).thenReturn(dto1);
        when(userMapper.toDto(user2)).thenReturn(dto2);

        mockMvc.perform(get("/api/users"))
                .andExpect(status().isOk()) // статус 200 OK
                .andExpect(jsonPath("$.length()").value(2)) // в ответе массив из 2 элементов
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].name").value("John"))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].name").value("Alice"));

        // Проверка, что метод findAll() был вызван один раз
        verify(userRepository, times(1)).findAll();
    }

    @Test
    void updateUser_shouldReturnUpdatedUser_whenUserExists() throws Exception {
        Long userId = 1L;
        User existingUser = new User();
        existingUser.setId(userId);
        existingUser.setName("OldName");
        existingUser.setEmail("old@example.com");
        existingUser.setAge(20);
        existingUser.setCreated_at(new Timestamp(System.currentTimeMillis() - 100000));

        UserDto updateDto = new UserDto();
        updateDto.setName("NewName");
        updateDto.setEmail("new@example.com");
        updateDto.setAge(30);

        User updatedUser = new User();
        updatedUser.setId(userId);
        updatedUser.setName("NewName");
        updatedUser.setEmail("new@example.com");
        updatedUser.setAge(30);
        updatedUser.setCreated_at(existingUser.getCreated_at());

        UserDto responseDto = new UserDto();
        responseDto.setId(userId);
        responseDto.setName("NewName");
        responseDto.setEmail("new@example.com");
        responseDto.setAge(30);
        responseDto.setCreated_at(existingUser.getCreated_at());

        when(userRepository.findById(userId)).thenReturn(Optional.of(existingUser));
        when(userMapper.toUpdatedEntity(any(UserDto.class))).thenReturn(updatedUser);
        when(userRepository.save(any(User.class))).thenReturn(updatedUser);
        when(userMapper.toDto(updatedUser)).thenReturn(responseDto);

        mockMvc.perform(put("/api/users/{id}", userId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk()) // 200 OK
                .andExpect(jsonPath("$.id").value(userId))
                .andExpect(jsonPath("$.name").value("NewName"))
                .andExpect(jsonPath("$.email").value("new@example.com"))
                .andExpect(jsonPath("$.age").value(30));

        verify(userRepository, times(1)).findById(userId);
        verify(userMapper, times(1)).toUpdatedEntity(any(UserDto.class));
        verify(userRepository, times(1)).save(any(User.class));
        verify(userMapper, times(1)).toDto(updatedUser);
    }

    @Test
    void deleteUser_shouldReturnNoContent_whenUserExists() throws Exception {
        Long userId = 1L;
        when(userRepository.existsById(userId)).thenReturn(true);
        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNoContent()); // 204 No Content

        verify(userRepository, times(1)).deleteById(userId);
        verify(userRepository, times(1)).existsById(userId);
    }

    @Test
    void deleteUser_shouldReturnNotFound_whenUserDoesNotExist() throws Exception {
        Long userId = 99L;
        when(userRepository.existsById(userId)).thenReturn(false);

        mockMvc.perform(delete("/api/users/{id}", userId))
                .andExpect(status().isNotFound()); // 404
        verify(userRepository, never()).deleteById(userId);
    }


}



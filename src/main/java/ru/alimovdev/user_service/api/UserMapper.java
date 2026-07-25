package ru.alimovdev.user_service.api;

import org.springframework.stereotype.Component;
import ru.alimovdev.user_service.model.User;

import java.sql.Timestamp;

@Component
public class UserMapper {

    // Для создания UserDto из User
    public UserDto toDto(User user) {
        if (user == null) return null;
        UserDto dto = new UserDto();
        dto.setId(user.getId());
        dto.setName(user.getName());
        dto.setEmail(user.getEmail());
        dto.setAge(user.getAge());
        dto.setCreated_at(user.getCreated_at());
        return dto;
    }

    // Для создания нового User
    public User toNewEntity(UserDto dto) {
        if (dto == null) return null;
        User user = new User();
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setAge(dto.getAge());
        user.setCreated_at(new Timestamp(System.currentTimeMillis()));
        return user;
    }

    // Для обновления существующего User
    public User toUpdatedEntity(UserDto dto) {
        if (dto == null) return null;
        User user = new User();
        user.setId(dto.getId());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setAge(dto.getAge());
        return user;
    }

}
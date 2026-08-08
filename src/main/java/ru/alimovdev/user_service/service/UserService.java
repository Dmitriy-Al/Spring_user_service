package ru.alimovdev.user_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.alimovdev.user_service.api.UserDto;
import ru.alimovdev.user_service.api.UserMapper;
import ru.alimovdev.user_service.model.User;
import ru.alimovdev.user_service.repository.UserRepository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.alimovdev.user_service.api.Event.*;

@Service
@Transactional
@Slf4j
public class UserService { // Логика из контроллера переехала в сервис
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper,
                       KafkaProducerService kafkaProducerService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.kafkaProducerService = kafkaProducerService;
    }

    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toNewEntity(userDto);
        user.setCreated_at(new Timestamp(System.currentTimeMillis()));
        User saved = userRepository.save(user);
        kafkaProducerService.sendUserEvent(CREATE.getEvent(), saved.getEmail());
        return userMapper.toDto(saved);
    }

    public Optional<UserDto> updateUser(Long id, UserDto userDto) {
        return userRepository.findById(id)
                .map(existing -> {
                    User user = userMapper.toUpdatedEntity(userDto);
                    user.setId(id);
                    user.setCreated_at(existing.getCreated_at());
                    User updated = userRepository.save(user);
                    return userMapper.toDto(updated);
                });
    }

    public boolean deleteUser(Long id) {
        return userRepository.findById(id)
                .map(user -> {
                    String email = user.getEmail();
                    userRepository.deleteById(id);
                    kafkaProducerService.sendUserEvent(DELETE.getEvent(), email);
                    return true;
                })
                .orElse(false);
    }

    public Optional<UserDto> getUser(Long id) {
        return userRepository.findById(id).map(userMapper::toDto);
    }

    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }
}
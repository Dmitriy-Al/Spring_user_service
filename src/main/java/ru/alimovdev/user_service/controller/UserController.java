package ru.alimovdev.user_service.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.alimovdev.user_service.api.UserDto;
import ru.alimovdev.user_service.api.UserMapper;
import ru.alimovdev.user_service.model.User;
import ru.alimovdev.user_service.repository.UserRepository;
import ru.alimovdev.user_service.service.KafkaProducerService;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static ru.alimovdev.user_service.api.Event.*;

@Slf4j
@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final KafkaProducerService kafkaProducerService;

    @Autowired
    public UserController(UserRepository userRepository,
                          UserMapper userMapper,
                          KafkaProducerService kafkaProducerService) {
        this.kafkaProducerService = kafkaProducerService;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    // GET /api/users/{id}
    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getUser(@PathVariable Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // GET /api/users
    @GetMapping
    public List<UserDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    // POST /api/users
    @PostMapping
    public ResponseEntity<UserDto> createUser(@RequestBody UserDto userDto) {
        User user = userMapper.toNewEntity(userDto);
        user.setCreated_at(new Timestamp(System.currentTimeMillis()));
        User saved = userRepository.save(user);
        // Отправление события в Kafka
        kafkaProducerService.sendUserEvent(CREATE.getEvent(), saved.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toDto(saved));
    }

    // PUT /api/users/{id}
    @PutMapping("/{id}")
    public ResponseEntity<UserDto> updateUser(@PathVariable Long id, @RequestBody UserDto userDto) {
        Optional<User> optionalUser = userRepository.findById(id);
        if (optionalUser.isPresent()) {
            User user = userMapper.toUpdatedEntity(userDto);
            user.setId(id); // гарантия что id совпадет
            user.setCreated_at(optionalUser.get().getCreated_at());
            User updated = userRepository.save(user);
            return ResponseEntity.ok(userMapper.toDto(updated));
        } else {
            log.error("Entity not found: " + ResponseEntity.notFound().build());
            return ResponseEntity.notFound().build();
        }
    }

    // DELETE /api/users/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        } else {
            String email = userRepository.findById(id).get().getEmail();
            userRepository.deleteById(id);
            // Отправление события в Kafka
           kafkaProducerService.sendUserEvent(DELETE.getEvent(), email);
            return ResponseEntity.noContent().build();
        }
    }
}
package ru.alimovdev.user_service.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.alimovdev.user_service.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

}

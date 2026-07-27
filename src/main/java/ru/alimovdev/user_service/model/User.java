package ru.alimovdev.user_service.model;

import jakarta.persistence.*;
import java.sql.Timestamp;

@lombok.Setter
@lombok.Getter
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int age;
    private String name;
    private String email;
    private Timestamp created_at;

    public User() {}

    @Override
    public String toString() {
        return "\nUser{id=" + id + ", age=" + age +
                ", name='" + name + ", email='" + email +
                ", created_at=" + created_at + '}';
    }

}

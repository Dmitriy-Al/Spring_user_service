package ru.alimovdev.user_service.api;

import java.sql.Timestamp;

@lombok.Setter
@lombok.Getter
public class UserDto {
    private Long id;
    private String name;
    private String email;
    private Integer age;
    private Timestamp created_at;


    public UserDto() {}


}


package ru.alimovdev.user_service.api;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserEvent {
    private String operation; // "CREATE" или "DELETE"
    private String email;

    public UserEvent() {}

    public UserEvent(String operation, String email) {
        this.operation = operation;
        this.email = email;
    }

}
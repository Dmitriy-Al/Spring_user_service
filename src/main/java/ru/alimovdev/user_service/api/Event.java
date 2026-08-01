package ru.alimovdev.user_service.api;

public enum Event {

    CREATE("CREATE"),
    DELETE("DELETE");

    private final String event;

    Event(String event) {
        this.event = event;
    }

    public String getEvent() {
        return event;
    }

}

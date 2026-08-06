package ru.alimovdev.user_service.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.alimovdev.user_service.api.UserEvent;

@Slf4j
@Service
public class KafkaProducerService {
    // Имя топика, в который отправляем сообщения
    private static final String TOPIC = "user-events";

    /*   KafkaTemplate — класс из модуля Spring для работы с Apache Kafka,
    удобная обёртка (шаблон) вокруг нативного Kafka Producer. Шаблон
    для отправки сообщений в Kafka (Key = String, Value = UserEvent) */
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Autowired
    public KafkaProducerService(KafkaTemplate<String, UserEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    /*    Отправляет событие о пользователе в Kafka.
    @param operation – тип операции: "CREATED" или "DELETED"
    @param email – email пользователя, к которому относится событие    */
    public void sendUserEvent(String operation, String email) {
        // Создаём объект события
        UserEvent event = new UserEvent(operation, email);

        // Отправка сообщения в топик. KafkaTemplate сериализует event в JSON и отправляет его брокеру.
        kafkaTemplate.send(TOPIC, event);
    }
}

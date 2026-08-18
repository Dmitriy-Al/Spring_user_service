package ru.alimovdev.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient // Spring cloud
public class  UserServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);

    }
}


/****************************************************************************************************************
Д.З. 4 - Добавить в user-service поддержку Spring и разработать API, которое позволит
управлять данными. Использовать необходимые модули spring(boot, web, data etc).
Реализовать api для получения, создания, обновления и удаления юзера.
Важно, entity не должен возвращаться из контроллера, необходимо использовать dto.
Заменить Hibernate на Spring data JPA.
Написать тесты для API(можно делать это при помощи mockMvc или других средств)
*****************************************************************************************************************
Д.З. 6 - Добавление Swagger-документации и HATEOAS в API. Задокументировать существующее
API (из задания 4) с помощью Swagger (Springdoc OpenAPI), чтобы можно было легко изучить
и тестировать API через веб-интерфейс. Добавить поддержку HATEOAS, чтобы API предоставляло
ссылки для навигации по ресурсам.
*****************************************************************************************************************
Добавить к существующей системе паттерны: gateway api, service discovery, circuit breaker, external configuration -
реализации данных паттернов можно найти в модулях spring cloud.

External Configuration (Spring Cloud Config)
External Configuration выносит все настройки приложений (БД, Kafka, порты, логирование) в централизованное хранилище
(обычно Git-репозиторий). Сервисы при запуске подтягивают конфигурацию оттуда. Это позволяет:
Менять настройки без пересборки и перезапуска сервисов.
Иметь разные конфигурации для разных окружений (dev, test, prod).
Хранить все настройки в одном месте.
*/

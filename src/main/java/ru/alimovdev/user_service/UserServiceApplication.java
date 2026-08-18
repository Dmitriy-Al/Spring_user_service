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

//    username: dmitriy
//    password: 121219841212
//    username: ${DB_USERNAME:}
//    password: ${DB_PASSWORD:}
// application.yml

/*
logback.xml
<configuration>
    <property name="HOME_LOG" value="C:/user_service_logs/user_service.log"/>

    <appender name="FILE-ROLLING" class="ch.qos.logback.core.rolling.RollingFileAppender">
        <file>${HOME_LOG}</file>

        <rollingPolicy class="ch.qos.logback.core.rolling.SizeAndTimeBasedRollingPolicy">

            <fileNamePattern>C:\Collect.bot.log\app.%d{yyyy-MM-dd}.%i.log.gz</fileNamePattern> <!--  -->
            <!-- each archived file, size max 10MB -->
            <maxFileSize>10MB</maxFileSize>
            <!-- total size of all archive files, if total size > 20GB, it will delete old archived file -->
            <totalSizeCap>100MB</totalSizeCap>
            <!-- 60 days to keep -->
            <maxHistory>60</maxHistory>
        </rollingPolicy>

        <encoder>
            <pattern>%d %p %c{1} [%t] %m%n</pattern>
        </encoder>
    </appender>

    <logger name="com.dev.mproject" level="debug" additivity="false">
        <appender-ref ref="FILE-ROLLING"/>
    </logger>

    <root level="error">
        <appender-ref ref="FILE-ROLLING"/>
    </root>

    <root level="info">
        <appender-ref ref="FILE-ROLLING"/>
    </root>
</configuration>
 */


/*
как NotificationController будет определять от кого поступил запрос, от авторизованного клиента, или от неавторизованного:
Когда клиент отправляет запрос с заголовком Authorization: Basic ..., этот заголовок перехватывается цепочкой фильтров
Spring Security ещё до того, как запрос попадёт в ваш NotificationController.
BasicAuthenticationFilter — это специальный фильтр, встроенный в Spring Security. Он:
Извлекает логин и пароль из заголовка Authorization.
Проверяет их через AuthenticationManager.
Если логин/пароль не совпадают с теми, что заданы в UserDetailsService, фильтр отклоняет запрос и возвращает клиенту
статус 401 Unauthorized, даже не вызывая контроллер.
Только если аутентификация прошла успешно, Spring Security создаёт аутентифицированный контекст (SecurityContext),
помещает его в SecurityContextHolder и передаёт запрос дальше по цепочке — в ваш контроллер.
Что происходит в контроллере
К моменту, когда запрос достигает метода sendEmail, он уже прошёл аутентификацию. В коде контроллера вы ничего не
проверяете — вся логика авторизации выполнена фильтрами.
Почему контроллер "не знает" о клиенте
В обычных условиях контроллеру не нужно знать, кто отправил запрос — это задача уровня безопасности. Spring Security
абстрагирует этот механизм и позволяет контроллеру фокусироваться на бизнес-логике.
Если запрос неавторизован, он даже не дойдёт до контроллера — клиент получит 401 Unauthorized от фильтра. Если
авторизован — контроллер отработает как обычно.

Авторизация: определяет Spring Security до контроллера.
Контроллер: получает запрос только после успешной аутентификации.
Если вы хотите знать, кто отправил запрос, можно получить объект Authentication в методе контроллера.
Если у вас есть ещё вопросы по этому механизму или другим замечаниям — я готов помочь.
 */

/*
spring:
  application:
    name: user_service

  config:
    import: "optional:configserver:http://localhost:8888"  # Современный способ подключения к Config Server [8†L43-L45], optional - при недоступности config

  datasource:
    url: jdbc:postgresql://localhost:5432/aston_study_db
    username: ${DB_USERNAME:dmitriy}
    password: ${DB_PASSWORD:121219841212}
    driver-class-name: org.postgresql.Driver

  jpa:
    hibernate:
      ddl-auto: update
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    show-sql: true

  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      properties:
        spring.json.add.type.headers: false

  cors:
    allowed-origins: "*"
    # http://localhost:3000,http://127.0.0.1:3000
    # "*"

    eureka:
      client:
        service-url:
          defaultZone: http://localhost:8761/eureka/
 */

/*
spring:
  application:
    name: user_service
  config:
    import: "optional:configserver:http://localhost:8888"
 */

/** конфиг из гит
 spring:
 datasource:
 url: jdbc:postgresql://localhost:5432/aston_study_db
 username: ${DB_USERNAME:dmitriy}
 password: ${DB_PASSWORD:121219841212}
 driver-class-name: org.postgresql.Driver

 jpa:
 hibernate:
 ddl-auto: update
 database-platform: org.hibernate.dialect.PostgreSQLDialect
 show-sql: true

 kafka:
 bootstrap-servers: localhost:9092
 producer:
 key-serializer: org.apache.kafka.common.serialization.StringSerializer
 value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
 properties:
 spring.json.add.type.headers: false

 cors:
 allowed-origins: "*"

 eureka:
 client:
 service-url:
 defaultZone: http://localhost:8761/eureka/

 */
/*
spring:
  jpa:
    hibernate:
      ddl-auto: update
    database-platform: org.hibernate.dialect.PostgreSQLDialect
    show-sql: true
  kafka:
    bootstrap-servers: localhost:9092
    producer:
      key-serializer: org.apache.kafka.common.serialization.StringSerializer
      value-serializer: org.springframework.kafka.support.serializer.JsonSerializer
      properties:
        spring.json.add.type.headers: false
  cors:
    allowed-origins: "*"
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
 */
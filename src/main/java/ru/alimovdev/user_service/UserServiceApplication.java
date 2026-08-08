package ru.alimovdev.user_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
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
 */

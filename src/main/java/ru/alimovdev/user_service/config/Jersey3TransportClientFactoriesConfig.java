package ru.alimovdev.user_service.config;

import com.netflix.discovery.shared.transport.jersey3.Jersey3TransportClientFactories;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Jersey3TransportClientFactoriesConfig {
    /*
     Этот конфигурационный класс создаёт бин Jersey3TransportClientFactories, который необходим для
     работы Eureka Client в Spring Cloud последних версий (начиная с 2023.0.x). В новых версиях
     Spring Cloud автоматическая конфигурация Eureka больше не создаёт бин TransportClientFactories
     автоматически. Без этого бина Eureka Client не может отправлять HTTP-запросы к Eureka Server
     (регистрация, получение списка сервисов и т.д.).
     */

    @Bean
    public Jersey3TransportClientFactories jersey3TransportClientFactories() {
        /*      Jersey3TransportClientFactories — это реализация интерфейса TransportClientFactories,
        которая использует Jersey 3 для выполнения HTTP-запросов. Бин с таким именем автоматически
        подхватывается авто-конфигурацией Spring Cloud Netflix Eureka (EurekaClientAutoConfiguration)
        и используется для создания EurekaHttpClient. Без этого бина при запуске приложения
        возникает ошибка. Зависимость eureka-client-jersey3 не создаёт бин автоматически — она только
        предоставляет класс Jersey3TransportClientFactories. Конфигурационный класс «поднимает» его
        до уровня Spring-бина, чтобы Spring мог его внедрить. Без него приложение не сможет
        зарегистрироваться в Eureka Server.    */
        return new Jersey3TransportClientFactories();
    }

}
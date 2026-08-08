package ru.alimovdev.user_service.controller;

import lombok.extern.log4j.Log4j;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.HashMap;
import java.util.Map;

/*    @RestControllerAdvice позволяет централизованно обрабатывать исключения во всём приложении. Она работает как
перехватчик (AOP-прокси) для всех контроллеров, помеченных @RestController (или @Controller с @ResponseBody).
Когда любой контроллер выбрасывает исключение, Spring сначала проверяет, есть ли метод в @RestControllerAdvice,
который может его обработать (по аннотации @GlobalExceptionHandler).    */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // Метод, помеченный @ExceptionHandler(SomeException.class), будет вызван, если в любом
    // контроллере возникнет исключение указанного типа
    @ExceptionHandler(MethodArgumentNotValidException.class) // выброс MethodArgumentNotValidException вызовет данный метод
    public ResponseEntity<Map<String, String>> handleValidateExceptions(
            MethodArgumentNotValidException exc, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        // BindingResult — это объект, который содержит результаты валидации: есть ли ошибки, сколько ошибок, по
        // каким полям, какие сообщения и т.д. Он используется внутри Spring для хранения информации о проверке.
        exc.getBindingResult().getFieldErrors().forEach(error -> errors.put(error.getField(),
                error.getDefaultMessage()));

        // getDescription(false) - метод интерфейса WebRequest из Spring Framework, возвращает строковое описание
        // текущего запроса, которое обычно включает HTTP-метод и URI. false - не включать информацию о сессии
        log.warn("Validation failed for request {}: {}", request.getDescription(false), errors);

        return ResponseEntity.badRequest().body(errors);
    }

}

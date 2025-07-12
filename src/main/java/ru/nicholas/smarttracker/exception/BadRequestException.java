package ru.nicholas.smarttracker.exception;

/**
 * Исключение, которое выбрасывается, когда запрос клиента имеет ошибочные или некорректные данные.
 */
public class BadRequestException extends RuntimeException {
    /**
     * Конструктор для создания нового исключения с заданным сообщением.
     *
     * @param message сообщение об ошибке, которое будет передано в исключение
     */
    public BadRequestException(String message) {
        super(message);
    }
}

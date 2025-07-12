package ru.nicholas.smarttracker.exception;

/**
 * Исключение, которое выбрасывается, когда запрашиваемый ресурс не найден.
 */
public class NotFoundException extends RuntimeException {
    /**
     * Конструктор для создания нового исключения с заданным сообщением.
     *
     * @param message сообщение об ошибке, которое будет передано в исключение
     */
    public NotFoundException(String message) {
        super(message);
    }
}

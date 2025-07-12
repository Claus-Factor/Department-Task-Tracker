package ru.nicholas.smarttracker.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;

/**
 * DTO для передачи информации об ошибке клиенту
 */
@Schema(name = "Сообщение об ошибке", description = "Объект, представляющий сообщение об ошибке")
@Data
@AllArgsConstructor
public class ErrorResponseDto {
    /**
     * Сообщение об ошибке
     */
    @Schema(description = "Текст сообщения об ошибке")
    private String message;
    /**
     * Дата и время ошибки
     */
    @Schema(description = "Дата-время ошибки")
    private LocalDateTime timestamp;
}

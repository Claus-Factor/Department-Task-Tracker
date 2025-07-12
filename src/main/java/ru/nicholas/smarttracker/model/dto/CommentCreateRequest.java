package ru.nicholas.smarttracker.model.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CommentCreateRequest {
    @NotBlank(message = "Комментарий не должен быть пустым")
    private String text;
}
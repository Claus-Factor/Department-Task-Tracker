package ru.nicholas.smarttracker.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nicholas.smarttracker.util.Priority;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskUpdateRequest {
    @NotBlank(message = "Заголовок не должен быть null")
    private String title;
    
    private String description;
    
    @NotNull(message = "Приоритет не должен быть null")
    private Priority priority;
    
    private LocalDateTime deadline;
    
    @NotNull(message = "ID исполнителя не должен быть null")
    private Long assigneeId;
}
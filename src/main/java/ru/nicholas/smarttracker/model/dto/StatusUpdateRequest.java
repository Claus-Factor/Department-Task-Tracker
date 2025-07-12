package ru.nicholas.smarttracker.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nicholas.smarttracker.util.Status;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StatusUpdateRequest {
    @NotNull(message = "Статус не должен быть null")
    private Status status;
}
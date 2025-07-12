package ru.nicholas.smarttracker.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nicholas.smarttracker.util.Role;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentDto {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private Long employeeCount;
    private String managerName;
}

package ru.nicholas.smarttracker.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nicholas.smarttracker.model.enity.Department;
import ru.nicholas.smarttracker.util.Role;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserDto {
    private Long id;
    private String username;
    private String fullName;
    private String email;
    private Role role;
    private String departmentName;
}

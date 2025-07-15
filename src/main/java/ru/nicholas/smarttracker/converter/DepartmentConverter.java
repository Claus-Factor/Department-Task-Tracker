package ru.nicholas.smarttracker.converter;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.data.domain.Page;
import ru.nicholas.smarttracker.model.dto.*;
import ru.nicholas.smarttracker.model.enity.Department;
import ru.nicholas.smarttracker.model.enity.User;
import ru.nicholas.smarttracker.util.Role;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DepartmentConverter {

    Department toEntity(DepartmentCreateRequest departmentCreateRequest);

    DepartmentShortDto toShortDto(Department department);

    List<DepartmentShortDto> toShortDtoList(List<Department> departments);

    default DepartmentDto toDto(Department department) {
        if (department == null) {
            return null;
        }

        DepartmentDto dto = new DepartmentDto();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setDescription(department.getDescription());
        dto.setCreatedAt(department.getCreatedAt());

        if (department.getEmployees() != null) {
            dto.setEmployeeCount((long) department.getEmployees().size());
        } else {
            dto.setEmployeeCount(0L);
        }

        if (department.getEmployees() != null) {
            String managerName = department.getEmployees().stream()
                    .filter(user -> user.getRole().equals(Role.ROLE_MANAGER))
                    .map(User::getFullName)
                    .collect(Collectors.joining());
            dto.setManagerName(managerName);
        } else {
            dto.setManagerName(null);
        }

        return dto;
    }

    default List<DepartmentDto> toDtoList(List<Department> departments) {
        return departments.stream().map(this::toDto).toList();
    }

    default Page<DepartmentDto> toDtoPage(Page<Department> departments) {
        return departments.map(this::toDto);
    }
}

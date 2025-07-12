package ru.nicholas.smarttracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.nicholas.smarttracker.converter.DepartmentConverter;
import ru.nicholas.smarttracker.exception.NotFoundException;
import ru.nicholas.smarttracker.model.dto.DepartmentCreateRequest;
import ru.nicholas.smarttracker.model.dto.DepartmentDto;
import ru.nicholas.smarttracker.model.dto.ErrorResponseDto;
import ru.nicholas.smarttracker.model.enity.Department;
import ru.nicholas.smarttracker.model.enity.User;
import ru.nicholas.smarttracker.repository.DepartmentRepository;
import ru.nicholas.smarttracker.service.UserService;
import ru.nicholas.smarttracker.util.Role;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/departments")
@RequiredArgsConstructor
@Tag(name = "Управление отделами")
public class DepartmentController {
    private final DepartmentRepository departmentRepository;
    private final DepartmentConverter departmentConverter;
    private final UserService userService;

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Получение всех отделов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получены отделы"),
            @ApiResponse(responseCode = "400", description = "Не корректно введены параметры"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (недостаточно прав)")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.OK)
    public Page<DepartmentDto> getAllDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        User currentUser = userService.getCurrentUser();

        if (currentUser.getRole().equals(Role.ROLE_MANAGER)) {
            return departmentConverter.toDtoPage(departmentRepository.findAllByName(pageable, currentUser.getDepartment().getName()));
        }
        return departmentConverter.toDtoPage(departmentRepository.findAll(pageable));
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Удаление отдела")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Отдел успешно удален"),
            @ApiResponse(responseCode = "400", description = "Невозможно удалить отдел с сотрудниками"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (недостаточно прав)"),
            @ApiResponse(responseCode = "404", description = "Отдел не найден")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public DepartmentDto deleteDepartment(@PathVariable Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Отдел не найден"));
        departmentRepository.delete(department);
        return departmentConverter.toDto(department);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Создание нового отдела")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Отдел успешно создан"),
            @ApiResponse(responseCode = "400", description = "Некорректные данные отдела"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (недостаточно прав)")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('ADMIN')")
    public DepartmentDto createDepartment(@RequestBody DepartmentCreateRequest request) {
        Department department = departmentConverter.toEntity(request);
        return departmentConverter.toDto(departmentRepository.save(department));
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDto handleAccessDenied(AccessDeniedException e) {
        return new ErrorResponseDto(e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotFound(NotFoundException e) {
        return new ErrorResponseDto(e.getMessage(), LocalDateTime.now());
    }
}

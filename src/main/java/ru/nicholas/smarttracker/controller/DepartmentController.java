package ru.nicholas.smarttracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.nicholas.smarttracker.exception.NotFoundException;
import ru.nicholas.smarttracker.model.dto.DepartmentCreateRequest;
import ru.nicholas.smarttracker.model.dto.DepartmentDto;
import ru.nicholas.smarttracker.model.dto.DepartmentShortDto;
import ru.nicholas.smarttracker.model.dto.ErrorResponseDto;
import ru.nicholas.smarttracker.service.DepartmentService;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("api/departments")
@RequiredArgsConstructor
@Tag(name = "Управление отделами")
public class DepartmentController {
    private final DepartmentService departmentService;

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
        return departmentService.getAllDepartments(page, size, sortBy, direction);
    }

    @Operation(summary = "Получение списка id и имён отделов")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получен список"),
            @ApiResponse(responseCode = "400", description = "Не корректно введены параметры"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (недостаточно прав)")
    })
    @GetMapping("/list")
    @ResponseStatus(HttpStatus.OK)
    public List<DepartmentShortDto> getDepartmentIdNameList() {
        return departmentService.getDepartmentIdNameList();
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
        return departmentService.deleteDepartment(id);
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
        return departmentService.createDepartment(request);
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

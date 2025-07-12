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
import ru.nicholas.smarttracker.exception.BadRequestException;
import ru.nicholas.smarttracker.exception.NotFoundException;
import ru.nicholas.smarttracker.model.dto.ErrorResponseDto;
import ru.nicholas.smarttracker.model.dto.UpdateUserRequest;
import ru.nicholas.smarttracker.model.dto.UserDto;
import ru.nicholas.smarttracker.service.UserService;
import ru.nicholas.smarttracker.util.Role;

import java.time.LocalDateTime;

@Tag(name = "Управление пользователями (сотрудниками)")
@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Получение пользователя по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получен пользователь по указанному id"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (недостаточно прав)")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER') or #id == principal.id")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getUserById(@PathVariable Long id) {
        return userService.getUserById(id);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Получение данных текущего пользователя")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получены данные пользователя"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
    })
    @GetMapping("/me")
    @ResponseStatus(HttpStatus.OK)
    public UserDto getMyProfile() {
        return userService.getMyProfile();
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Обновление пользователя по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно обновлен пользователь по указанному id"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Не корректно введены данные в DTO"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (недостаточно прав)")
    })
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal.id")
    public UserDto updateUserById(@PathVariable Long id, @RequestBody UpdateUserRequest updateUserRequest) {
        return userService.updateUserById(id, updateUserRequest);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Обновление роли по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно обновлена роль пользователя по указанному id"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "400", description = "Не корректно введена роль"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (недостаточно прав)")
    })
    @PutMapping("/{id}/role")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto updateRoleById(@PathVariable Long id, @RequestParam Role role) {
        return userService.updateRoleById(id, role);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Удаление пользователя по id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Успешно удалён пользователь по указанному id"),
            @ApiResponse(responseCode = "404", description = "Пользователь не найден"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (недостаточно прав)")
    })
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @ResponseStatus(HttpStatus.OK)
    public UserDto deleteUserById(@PathVariable Long id) {
        return userService.deleteUserById(id);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Получение нескольких пользователей")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получены несколько пользователей"),
            @ApiResponse(responseCode = "400", description = "Не корректно введены параметры"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (недостаточно прав)")
    })
    @GetMapping
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserDto> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        return userService.getAllUsers(page, size, sortBy, direction);
    }

    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(summary = "Получение пользователей по заданным роли и отделу")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Успешно получены несколько пользователей по роли"),
            @ApiResponse(responseCode = "400", description = "Не корректно введены параметры"),
            @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
            @ApiResponse(responseCode = "403", description = "Доступ запрещен (недостаточно прав)")
    })
    @GetMapping("/by-role")
    @PreAuthorize("hasRole('ADMIN') or hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.OK)
    public Page<UserDto> getAllUsersByRoleAndDepartment(
            @RequestParam Role role,
            @RequestParam(required = false) Long departmentId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "ASC") Sort.Direction direction
    ) {
        return userService.getAllUsersByRoleAndDepartment(role, departmentId, page, size, sortBy, direction);
    }

    /**
     * Обработчик исключения {@link BadRequestException}.
     * При возникновении этого исключения, возвращается статус {@link HttpStatus#BAD_REQUEST} и сообщение об ошибке.
     *
     * @param e исключение {@link BadRequestException}, которое возникло
     * @return сообщение об ошибке
     */
    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleBadRequest(BadRequestException e) {
        return new ErrorResponseDto(e.getMessage(), LocalDateTime.now());
    }

    /**
     * Обработчик исключения {@link NotFoundException}.
     * При возникновении этого исключения, возвращается статус {@link HttpStatus#NOT_FOUND} и сообщение об ошибке.
     *
     * @param e исключение {@link NotFoundException}, которое возникло
     * @return сообщение об ошибке
     */
    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotFound(NotFoundException e) {
        return new ErrorResponseDto(e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponseDto handleAccessDenied(AccessDeniedException e) {
        return new ErrorResponseDto(e.getMessage(), LocalDateTime.now());
    }

}

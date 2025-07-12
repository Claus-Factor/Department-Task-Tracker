package ru.nicholas.smarttracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import ru.nicholas.smarttracker.exception.BadRequestException;
import ru.nicholas.smarttracker.exception.NotFoundException;
import ru.nicholas.smarttracker.model.dto.*;
import ru.nicholas.smarttracker.service.CommentService;
import ru.nicholas.smarttracker.service.TaskService;
import ru.nicholas.smarttracker.util.Priority;
import ru.nicholas.smarttracker.util.Status;

import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Управление задачами и комментариями")
@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {
    private final TaskService taskService;
    private final CommentService commentService;

    @Operation(
            summary = "Получить список задач",
            description = "Возвращает страницу задач с возможностью фильтрации и сортировки",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Успешное получение списка задач"),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public Page<TaskDto> getAllTasks(
            @RequestParam(required = false) Status status,
            @RequestParam(required = false) Priority priority,
            @RequestParam(required = false) Long assigneeId,
            @RequestParam(required = false) Long assignerId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime deadlineFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime deadlineTo,
            @RequestParam(required = false) String search,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortOrder,
            @RequestParam(required = false, defaultValue = "0") Integer page,
            @RequestParam(required = false, defaultValue = "6") Integer size
    ) {

        Sort.Direction direction = Sort.Direction.fromString(sortOrder);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return taskService.getAllTasks(
                status, priority, assigneeId, assignerId,
                deadlineFrom, deadlineTo, search, pageable
        );
    }

    @Operation(
            summary = "Создать новую задачу",
            description = "Доступно только для пользователей с ролью MANAGER",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Задача успешно создана"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные задачи"),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен")
            })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('MANAGER')")
    public TaskDto createTask(@RequestBody TaskCreateRequest createRequest) {
        return taskService.createTask(createRequest);
    }

    @Operation(
            summary = "Получить задачу по ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Задача найдена"),
                    @ApiResponse(responseCode = "404", description = "Задача не найдена"),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован")
            })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public TaskDto getTaskById(@PathVariable Long id) {
        return taskService.getTaskById(id);
    }

    @Operation(
            summary = "Обновить задачу",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Задача успешно обновлена"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные задачи"),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Задача не найдена")
            })
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    @PreAuthorize("hasRole('MANAGER')")
    public TaskDto updateTask(@PathVariable Long id,
                              @RequestBody @Valid TaskUpdateRequest updateRequest) {
        return taskService.updateTask(id, updateRequest);
    }

    @Operation(
            summary = "Обновить статус задачи",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Статус задачи успешно обновлен"),
                    @ApiResponse(responseCode = "400", description = "Некорректный статус"),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Задача не найдена")
            })
    @SecurityRequirement(name = "Bearer Authentication")
    @PutMapping("/{id}/status")
    @ResponseStatus(HttpStatus.OK)
    public TaskDto updateTaskStatus(@PathVariable Long id,
                                    @RequestBody @Valid StatusUpdateRequest statusUpdate) {
        return taskService.updateTaskStatus(id, statusUpdate.getStatus());
    }

    @Operation(
            summary = "Удалить задачу",
            description = "Доступно только для пользователей с ролью MANAGER",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Задача успешно удалена"),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Задача не найдена")
            })
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('MANAGER')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteTask(@PathVariable Long id) {
        taskService.deleteTask(id);
    }

    @Operation(
            summary = "Получить комментарии к задаче",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Список комментариев"),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
                    @ApiResponse(responseCode = "404", description = "Задача не найдена")
            })
    @SecurityRequirement(name = "Bearer Authentication")
    @GetMapping("/{taskId}/comments")
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> getTaskComments(@PathVariable Long taskId) {
        return commentService.getCommentsByTaskId(taskId);
    }

    @Operation(
            summary = "Добавить комментарий к задаче",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Комментарий успешно добавлен"),
                    @ApiResponse(responseCode = "400", description = "Некорректные данные комментария"),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
                    @ApiResponse(responseCode = "404", description = "Задача не найдена")
            })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/{taskId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto addCommentToTask(
            @PathVariable Long taskId,
            @RequestBody @Valid CommentCreateRequest createRequest) {
        return commentService.addCommentToTask(taskId, createRequest);
    }

    @Operation(
            summary = "Удалить комментарий",
            description = "Удалить может только автор комментария или администратор",
            responses = {
                    @ApiResponse(responseCode = "204", description = "Комментарий успешно удален"),
                    @ApiResponse(responseCode = "401", description = "Пользователь не авторизован"),
                    @ApiResponse(responseCode = "403", description = "Доступ запрещен"),
                    @ApiResponse(responseCode = "404", description = "Комментарий не найден")
            })
    @SecurityRequirement(name = "Bearer Authentication")
    @DeleteMapping("/comments/{commentId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteComment(
            @PathVariable Long commentId) {
        commentService.deleteComment(commentId);
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
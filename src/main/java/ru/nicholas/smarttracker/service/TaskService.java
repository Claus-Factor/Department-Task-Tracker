package ru.nicholas.smarttracker.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import ru.nicholas.smarttracker.converter.TaskConverter;
import ru.nicholas.smarttracker.exception.NotFoundException;
import ru.nicholas.smarttracker.model.dto.TaskCreateRequest;
import ru.nicholas.smarttracker.model.dto.TaskDto;
import ru.nicholas.smarttracker.model.dto.TaskUpdateRequest;
import ru.nicholas.smarttracker.model.enity.Task;
import ru.nicholas.smarttracker.model.enity.User;
import ru.nicholas.smarttracker.repository.TaskRepository;
import ru.nicholas.smarttracker.repository.UserRepository;
import ru.nicholas.smarttracker.util.Priority;
import ru.nicholas.smarttracker.util.Role;
import ru.nicholas.smarttracker.util.Status;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TaskService {
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskConverter taskConverter;

    public Page<TaskDto> getAllTasks(Status status, Priority priority,
                                     Long assigneeId, Long assignerId,
                                     LocalDateTime deadlineFrom, LocalDateTime deadlineTo, String search,
                                     Pageable pageable
    ) {

        User currentUser = getCurrentUser();
        Specification<Task> spec = Specification.where(null);

        // Фильтрация по ролям
        if (currentUser.getRole() == Role.ROLE_EMPLOYEE) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("assignee").get("id"), currentUser.getId()));
        }
        else if (currentUser.getRole() == Role.ROLE_MANAGER) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("assigner").get("id"), currentUser.getId()));
        }
        // ADMIN видит все задачи - без дополнительных условий

        // Поиск по подстроке (название или описание)
        if (StringUtils.hasText(search)) {
            String searchPattern = "%" + search.toLowerCase() + "%";
            spec = spec.and((root, query, cb) ->
                    cb.or(
                            cb.like(cb.lower(root.get("title")), searchPattern),
                            cb.like(cb.lower(root.get("description")), searchPattern)
                    ));
        }

        // Добавляем пользовательские фильтры
        if (status != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
        }
        if (priority != null) {
            spec = spec.and((root, query, cb) -> cb.equal(root.get("priority"), priority));
        }
        if (assigneeId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("assignee").get("id"), assigneeId));
        }
        if (assignerId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("assigner").get("id"), assignerId));
        }
        if (deadlineFrom != null && deadlineTo != null) {
            spec = spec.and((root, query, cb) ->
                    cb.between(root.get("deadline"), deadlineFrom, deadlineTo));
        }

        return taskRepository.findAll(spec, pageable).map(taskConverter::toDto);
    }

    @Transactional
    public TaskDto createTask(TaskCreateRequest createRequest) {
        User assigner = getCurrentUser();
        User assignee = userRepository.findById(createRequest.getAssigneeId())
                .orElseThrow(() -> new NotFoundException("Assignee not found"));

        Task task = new Task();
        task.setTitle(createRequest.getTitle());
        task.setDescription(createRequest.getDescription());
        task.setStatus(Status.NEW);
        task.setPriority(createRequest.getPriority());
        task.setCreatedAt(LocalDateTime.now());
        task.setDeadline(createRequest.getDeadline());
        task.setAssigner(assigner);
        task.setAssignee(assignee);

        Task savedTask = taskRepository.save(task);
        return taskConverter.toDto(savedTask);
    }

    public TaskDto getTaskById(Long id) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found"));

        User currentUser = getCurrentUser();

        // Проверка прав доступа
        if (currentUser.getRole() == Role.ROLE_EMPLOYEE &&
                !task.getAssignee().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only view your assigned tasks");
        }

        if (currentUser.getRole() == Role.ROLE_MANAGER &&
                !task.getAssigner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only view tasks you created");
        }

        return taskConverter.toDto(task);
    }

    @Transactional
    public TaskDto updateTask(Long id, TaskUpdateRequest updateRequest) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found"));

        User assignee = userRepository.findById(updateRequest.getAssigneeId())
                .orElseThrow(() -> new NotFoundException("Assignee not found"));

        User currentUser = getCurrentUser();

        // Проверка прав доступа

        if (currentUser.getRole() == Role.ROLE_MANAGER &&
                !task.getAssigner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only tasks tasks you created");
        }

        task.setTitle(updateRequest.getTitle());
        task.setDescription(updateRequest.getDescription());
        task.setPriority(updateRequest.getPriority());
        task.setDeadline(updateRequest.getDeadline());
        task.setAssignee(assignee);

        Task updatedTask = taskRepository.save(task);
        return taskConverter.toDto(updatedTask);
    }

    @Transactional
    public TaskDto updateTaskStatus(Long id, Status newStatus) {
        Task task = taskRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Task not found"));

        User currentUser = getCurrentUser();

        // Проверка прав доступа
        if (currentUser.getRole() == Role.ROLE_EMPLOYEE &&
                !task.getAssignee().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only view your assigned tasks");
        }

        if (currentUser.getRole() == Role.ROLE_MANAGER &&
                !task.getAssigner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only view tasks you created");
        }

        task.setStatus(newStatus);
        Task updatedTask = taskRepository.save(task);
        return taskConverter.toDto(updatedTask);
    }

    @Transactional
    public void deleteTask(Long id) {
        if (!taskRepository.existsById(id)) {
            throw new NotFoundException("Task not found");
        }
        taskRepository.deleteById(id);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("User not found"));
    }
}
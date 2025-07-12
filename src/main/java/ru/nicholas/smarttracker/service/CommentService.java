package ru.nicholas.smarttracker.service;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.nicholas.smarttracker.converter.CommentConverter;
import ru.nicholas.smarttracker.model.dto.CommentCreateRequest;
import ru.nicholas.smarttracker.model.dto.CommentDto;
import ru.nicholas.smarttracker.model.enity.Comment;
import ru.nicholas.smarttracker.model.enity.Task;
import ru.nicholas.smarttracker.model.enity.User;
import ru.nicholas.smarttracker.repository.CommentRepository;
import ru.nicholas.smarttracker.repository.TaskRepository;
import ru.nicholas.smarttracker.repository.UserRepository;
import ru.nicholas.smarttracker.util.Role;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final CommentConverter commentConverter;

    public List<CommentDto> getCommentsByTaskId(Long taskId) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        User currentUser = getCurrentUser();

        // Проверка прав доступа
        if (currentUser.getRole() == Role.ROLE_EMPLOYEE &&
                !task.getAssignee().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only add comment to your assigned tasks");
        }

        if (currentUser.getRole() == Role.ROLE_MANAGER &&
                !task.getAssigner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only add comment to task you created");
        }

        return commentRepository.findByTaskOrderByCreatedAtDesc(task).stream()
                .map(commentConverter::toDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public CommentDto addCommentToTask(Long taskId, CommentCreateRequest createRequest) {
        Task task = taskRepository.findById(taskId)
                .orElseThrow(() -> new EntityNotFoundException("Task not found"));

        User currentUser = getCurrentUser();

        // Проверка прав доступа
        if (currentUser.getRole() == Role.ROLE_EMPLOYEE &&
                !task.getAssignee().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only add comment to your assigned tasks");
        }

        if (currentUser.getRole() == Role.ROLE_MANAGER &&
                !task.getAssigner().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You can only add comment to task you created");
        }

        User author = getCurrentUser();

        Comment comment = commentConverter.toEntity(createRequest, author, task);
        Comment savedComment = commentRepository.save(comment);

        return commentConverter.toDto(savedComment);
    }

    @Transactional
    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new EntityNotFoundException("Comment not found"));
        log.info("Удаление комментария {}", comment);

        // Проверка, что текущий пользователь - автор комментария или админ
        User currentUser = getCurrentUser();
        if (!currentUser.getId().equals(comment.getAuthor().getId()) && !currentUser.getRole().equals(Role.ROLE_ADMIN)) {
            log.warn("Удалить комментарий может только автор");
            throw new AccessDeniedException("Удалить комментарий может только автор");
        }

        commentRepository.delete(comment);
    }

    private User getCurrentUser() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
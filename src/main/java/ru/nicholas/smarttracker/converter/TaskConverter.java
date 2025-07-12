package ru.nicholas.smarttracker.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.nicholas.smarttracker.model.dto.TaskDto;
import ru.nicholas.smarttracker.model.enity.Task;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class TaskConverter {
    private final UserConverter userConverter;
    private final CommentConverter commentConverter;

    public TaskDto toDto(Task task) {
        TaskDto dto = new TaskDto();
        dto.setId(task.getId());
        dto.setTitle(task.getTitle());
        dto.setDescription(task.getDescription());
        dto.setStatus(task.getStatus());
        dto.setPriority(task.getPriority());
        dto.setCreatedAt(task.getCreatedAt());
        dto.setDeadline(task.getDeadline());
        dto.setAssigner(userConverter.toDto(task.getAssigner()));
        dto.setAssignee(userConverter.toDto(task.getAssignee()));
        
        if (task.getComments() != null) {
            dto.setComments(task.getComments().stream()
                    .map(commentConverter::toDto)
                    .collect(Collectors.toList()));
        }
        
        return dto;
    }
}
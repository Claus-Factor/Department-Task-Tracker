package ru.nicholas.smarttracker.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.nicholas.smarttracker.util.Priority;
import ru.nicholas.smarttracker.util.Status;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TaskDto {
    private Long id;
    private String title;
    private String description;
    private Status status;
    private Priority priority;
    private LocalDateTime createdAt;
    private LocalDateTime deadline;
    private UserDto assigner;
    private UserDto assignee;
    private List<CommentDto> comments;
}
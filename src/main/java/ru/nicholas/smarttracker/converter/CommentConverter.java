package ru.nicholas.smarttracker.converter;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.nicholas.smarttracker.model.dto.CommentCreateRequest;
import ru.nicholas.smarttracker.model.dto.CommentDto;
import ru.nicholas.smarttracker.model.enity.Comment;
import ru.nicholas.smarttracker.model.enity.Task;
import ru.nicholas.smarttracker.model.enity.User;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class CommentConverter {
    private final UserConverter userConverter;

    public CommentDto toDto(Comment comment) {
        if (comment == null) {
            return null;
        }

        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .createdAt(comment.getCreatedAt())
                .author(userConverter.toDto(comment.getAuthor()))
                .build();
    }

    public Comment toEntity(CommentCreateRequest createRequest, User author, Task task) {
        return Comment.builder()
                .text(createRequest.getText())
                .createdAt(LocalDateTime.now())
                .author(author)
                .task(task)
                .build();
    }
}
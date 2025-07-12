package ru.nicholas.smarttracker.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nicholas.smarttracker.model.enity.Comment;
import ru.nicholas.smarttracker.model.enity.Task;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findByTaskOrderByCreatedAtDesc(Task task);
}

package ru.nicholas.smarttracker.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nicholas.smarttracker.model.enity.Department;
import ru.nicholas.smarttracker.model.enity.User;
import ru.nicholas.smarttracker.util.Role;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    Page<User> findAllByRole(Role role, Pageable pageable);
    Page<User> findAllByRoleAndDepartment(Role role, Department department, Pageable pageable);
    boolean existsByDepartmentId(Long id);
}
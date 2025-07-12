package ru.nicholas.smarttracker.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.nicholas.smarttracker.model.enity.Department;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {
    Page<Department> findAllByName(Pageable pageable, String name);
}

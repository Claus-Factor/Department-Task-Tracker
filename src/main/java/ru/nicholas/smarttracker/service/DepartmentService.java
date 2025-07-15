package ru.nicholas.smarttracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.nicholas.smarttracker.converter.DepartmentConverter;
import ru.nicholas.smarttracker.exception.NotFoundException;
import ru.nicholas.smarttracker.model.dto.DepartmentCreateRequest;
import ru.nicholas.smarttracker.model.dto.DepartmentDto;
import ru.nicholas.smarttracker.model.dto.DepartmentShortDto;
import ru.nicholas.smarttracker.model.enity.Department;
import ru.nicholas.smarttracker.model.enity.User;
import ru.nicholas.smarttracker.repository.DepartmentRepository;
import ru.nicholas.smarttracker.util.Role;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DepartmentService {
    private final DepartmentRepository departmentRepository;
    private final DepartmentConverter departmentConverter;
    private final UserService userService;

    public Page<DepartmentDto> getAllDepartments(int page, int size, String sortBy, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        User currentUser = userService.getCurrentUser();

        if (currentUser.getRole().equals(Role.ROLE_MANAGER)) {
            return departmentConverter.toDtoPage(departmentRepository.findAllByName(pageable, currentUser.getDepartment().getName()));
        }
        return departmentConverter.toDtoPage(departmentRepository.findAll(pageable));
    }

    public List<DepartmentShortDto> getDepartmentIdNameList() {
        return departmentConverter.toShortDtoList(departmentRepository.findAll());
    }

    public DepartmentDto deleteDepartment(Long id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Отдел не найден"));
        departmentRepository.delete(department);
        return departmentConverter.toDto(department);
    }

    public DepartmentDto createDepartment(DepartmentCreateRequest request) {
        Department department = departmentConverter.toEntity(request);
        return departmentConverter.toDto(departmentRepository.save(department));
    }
}

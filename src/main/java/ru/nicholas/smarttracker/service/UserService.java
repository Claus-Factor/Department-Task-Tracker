package ru.nicholas.smarttracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import ru.nicholas.smarttracker.converter.UserConverter;
import ru.nicholas.smarttracker.exception.BadRequestException;
import ru.nicholas.smarttracker.exception.NotFoundException;
import ru.nicholas.smarttracker.model.dto.SignUpRequest;
import ru.nicholas.smarttracker.model.dto.UpdateUserRequest;
import ru.nicholas.smarttracker.model.dto.UserDto;
import ru.nicholas.smarttracker.model.enity.Department;
import ru.nicholas.smarttracker.model.enity.User;
import ru.nicholas.smarttracker.repository.DepartmentRepository;
import ru.nicholas.smarttracker.repository.UserRepository;
import ru.nicholas.smarttracker.util.Role;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final UserConverter userConverter;
    private final DepartmentRepository departmentRepository;

    /**
     * Создание пользователя.
     * @return созданный пользователь
     */
    public User createUser(SignUpRequest signUpRequest) {
        log.info("Запрос на регистрацию: {}", signUpRequest);
        User user = userConverter.toEntity(signUpRequest);
        user.setRole(Role.ROLE_EMPLOYEE);
        log.info("Сущность нового сотрудника: {}", user);
        if (userRepository.existsByUsername(user.getUsername())) {
            throw new BadRequestException("Пользователь с таким именем уже существует");
        }
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new BadRequestException("Пользователь с таким email уже существует");
        }
        Department department = departmentRepository.findById(signUpRequest.getDepartmentId()).orElseThrow(
                () -> new NotFoundException("Не найден отдел с таким ID")
        );
        user.setDepartment(department);
        return userRepository.save(user);
    }

    /**
     * Получение пользователя по имени пользователя.
     * @return пользователь
     */
    public User getByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new NotFoundException("Пользователь не найден"));

    }

    /**
     * Получение пользователя по имени пользователя.
     * Нужен для Spring Security
     * @return пользователь
     */
    public UserDetailsService userDetailsService() {
        return this::getByUsername;
    }

    /**
     * Получение текущего пользователя
     * @return текущий пользователь
     */
    public User getCurrentUser() {
        var username = SecurityContextHolder.getContext().getAuthentication().getName();
        return getByUsername(username);
    }

    public UserDto getUserById(Long id) {
        log.info("Идентификатор пользователя: {}", id);
        UserDto user = userConverter.toDto(userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователь не найден"))
        );
        log.info("Найденный пользователь: {}", user);
        return user;
    }

    public Page<UserDto> getAllUsers(int page, int size, String sortBy, Sort.Direction direction) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
        return userConverter.toDtoPage(userRepository.findAll(pageable));
    }

    public Page<UserDto> getAllUsersByRoleAndDepartment(Role role, Long departmentId, int page, int size, String sortBy, Sort.Direction direction) {
        log.info("Введенные роль и ID отдела: {}, {}", role, departmentId);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));

        if (departmentId != null) {
            Department department = departmentRepository.findById(departmentId).orElse(null);

            if (getCurrentUser().getRole().equals(Role.ROLE_ADMIN)) {
                return userConverter.toDtoPage(userRepository.findAllByRoleAndDepartment(role, department, pageable));
            }
        } else {
            if (getCurrentUser().getRole().equals(Role.ROLE_ADMIN)) {
                return userConverter.toDtoPage(userRepository.findAllByRole(role, pageable));
            }
            Department departmentOfCurrentUser = getCurrentUser().getDepartment();
            return userConverter.toDtoPage(userRepository.findAllByRoleAndDepartment(role, departmentOfCurrentUser, pageable));
        }
        return null;

    }

    public UserDto updateUserById(Long id, UpdateUserRequest updateUserRequest) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователь не найден")
        );
        if (userRepository.existsByUsername(updateUserRequest.getUsername())) {
            throw new BadRequestException("Пользователь с таким именем уже существует");
        }
        user.setUsername(updateUserRequest.getUsername());
        if (userRepository.existsByEmail(updateUserRequest.getEmail())) {
            throw new BadRequestException("Пользователь с таким email уже существует");
        }
        user.setEmail(updateUserRequest.getEmail());
        userRepository.save(user);
        return userConverter.toDto(user);
    }

    public UserDto updateRoleById(Long id, Role role) {
        log.info("Идентификатор пользователя: {}", id);
        User user = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователь не найден")
        );
        log.info("Найденный пользователь: {}", user);
        if (user.getRole().equals(Role.ROLE_ADMIN)) {
            log.warn("Нельзя сменить роль для администратора");
            throw new BadRequestException("Нельзя сменить роль для администратора");
        }
        user.setRole(role);
        log.info("Обновлена роль: {}", user.getRole());
        userRepository.save(user);
        return userConverter.toDto(user);
    }

    public UserDto getMyProfile() {
        User me = getCurrentUser();
        log.info("Загружен профиль текущего пользователя");
        return userConverter.toDto(me);
    }

    public UserDto deleteUserById(Long id) {
        User userToDelete = userRepository.findById(id).orElseThrow(
                () -> new NotFoundException("Пользователь не найден")
        );
        userRepository.delete(userToDelete);
        return userConverter.toDto(userToDelete);
    }
}

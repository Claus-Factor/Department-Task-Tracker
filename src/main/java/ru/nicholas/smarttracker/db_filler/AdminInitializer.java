package ru.nicholas.smarttracker.db_filler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import ru.nicholas.smarttracker.model.enity.User;
import ru.nicholas.smarttracker.repository.UserRepository;
import ru.nicholas.smarttracker.util.Role;

@Slf4j
@Component
@RequiredArgsConstructor
public class AdminInitializer implements CommandLineRunner {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.fullname}")
    private String adminFullName;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Value("${app.admin.email}")
    private String adminEmail;

    @Override
    public void run(String... args) throws Exception {
        if (userRepository.findByUsername(adminUsername).isPresent()) {
            log.info("Админ уже зарегистрирован");
        } else {
            User admin = new User();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setEmail(adminEmail);
            admin.setFullName(adminFullName);
            admin.setRole(Role.ROLE_ADMIN);

            userRepository.save(admin);
            log.info("Админ инициализирован: {}", admin.toString());
        }
    }
}

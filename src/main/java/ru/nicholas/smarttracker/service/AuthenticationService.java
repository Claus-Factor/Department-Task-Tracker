package ru.nicholas.smarttracker.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import ru.nicholas.smarttracker.model.dto.JwtAuthenticationResponse;
import ru.nicholas.smarttracker.model.dto.SignInRequest;
import ru.nicholas.smarttracker.model.dto.SignUpRequest;
import ru.nicholas.smarttracker.model.enity.User;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    private final UserService userService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;

    /**
     * Регистрация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signUp(SignUpRequest request) {

        request.setPassword(passwordEncoder.encode(request.getPassword()));
        User user = userService.createUser(request);

        var jwt = jwtService.generateToken(user);
        return new JwtAuthenticationResponse(jwt);
    }

    /**
     * Аутентификация пользователя
     *
     * @param request данные пользователя
     * @return токен
     */
    public JwtAuthenticationResponse signIn(SignInRequest request) {
        authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                request.getUsername(),
                request.getPassword()
        ));

        var user = userService
                .userDetailsService()
                .loadUserByUsername(request.getUsername());
        log.info("Авторизован пользователь: {}", user.getUsername());

        var jwt = jwtService.generateToken(user);

        log.info("Выдан токен JWT: {}", jwt);
        return new JwtAuthenticationResponse(jwt);
    }
}

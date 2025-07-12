package ru.nicholas.smarttracker.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.nicholas.smarttracker.exception.BadRequestException;
import ru.nicholas.smarttracker.exception.NotFoundException;
import ru.nicholas.smarttracker.model.dto.ErrorResponseDto;
import ru.nicholas.smarttracker.model.dto.JwtAuthenticationResponse;
import ru.nicholas.smarttracker.model.dto.SignInRequest;
import ru.nicholas.smarttracker.model.dto.SignUpRequest;
import ru.nicholas.smarttracker.service.AuthenticationService;

import java.time.LocalDateTime;

@RestController
@RequestMapping("api/auth")
@RequiredArgsConstructor
@Tag(name = "Регистрация и аутентификация")
public class AuthController {
    private final AuthenticationService authenticationService;

    @Operation(summary = "Регистрация пользователя")
    @PostMapping("/sign-up")
    public JwtAuthenticationResponse signUp(@RequestBody @Valid SignUpRequest request) {
        return authenticationService.signUp(request);
    }

    @Operation(summary = "Авторизация пользователя")
    @PostMapping("/sign-in")
    public JwtAuthenticationResponse signIn(@RequestBody @Valid SignInRequest request) {
        return authenticationService.signIn(request);
    }

    @ExceptionHandler(BadRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponseDto handleBadRequest(BadRequestException e) {
        return new ErrorResponseDto(e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(NotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponseDto handleNotFound(NotFoundException e) {
        return new ErrorResponseDto(e.getMessage(), LocalDateTime.now());
    }
}

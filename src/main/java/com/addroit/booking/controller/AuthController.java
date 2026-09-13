package com.addroit.booking.controller;

import com.addroit.booking.constants.AuthConstants;
import com.addroit.booking.dto.ResponseDto;
import com.addroit.booking.dto.auth.AuthResponseDto;
import com.addroit.booking.dto.auth.LoginRequestDto;
import com.addroit.booking.dto.auth.RegisterRequestDto;
import com.addroit.booking.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(
        name = "Authentication",
        description = "User registration and login APIs"
)
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Register a user",
            description = "Registers a new organiser or attendee"
    )
    @SecurityRequirements
    public ResponseEntity<ResponseDto> register(
            @Valid @RequestBody RegisterRequestDto registerRequestDto) {

        authService.register(registerRequestDto);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(
                        AuthConstants.STATUS_201,
                        AuthConstants.MESSAGE_201,
                        null
                ));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Login user",
            description = "Authenticates a user and returns a JWT"
    )
    @SecurityRequirements
    public ResponseEntity<ResponseDto> login(
            @Valid @RequestBody LoginRequestDto loginRequestDto) {

        AuthResponseDto authResponseDto =
                authService.login(loginRequestDto);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(new ResponseDto(
                        AuthConstants.STATUS_200,
                        AuthConstants.MESSAGE_200,
                        authResponseDto
                ));
    }
}
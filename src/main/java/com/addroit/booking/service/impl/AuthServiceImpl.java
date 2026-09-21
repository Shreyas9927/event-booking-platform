package com.addroit.booking.service.impl;

import com.addroit.booking.constants.AuthConstants;
import com.addroit.booking.dto.auth.AuthResponseDto;
import com.addroit.booking.dto.auth.LoginRequestDto;
import com.addroit.booking.dto.auth.RegisterRequestDto;
import com.addroit.booking.entity.User;
import com.addroit.booking.exception.EmailAlreadyExistsException;
import com.addroit.booking.repository.UserRepository;
import com.addroit.booking.security.JwtService;
import com.addroit.booking.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Locale;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    @Override
    @Transactional
    public void register(RegisterRequestDto registerRequestDto) {

        // Normalize email to avoid duplicate accounts due to spaces or uppercase letters
        String normalizedEmail = registerRequestDto
                .getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        // Check whether the email is already registered
        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException(
                    AuthConstants.MESSAGE_409
            );
        }

        User user = User.builder()
                .name(registerRequestDto.getName().trim())
                .email(normalizedEmail)

                // Store password as BCrypt hash, never as plain text
                .passwordHash(
                        passwordEncoder.encode(
                                registerRequestDto.getPassword()
                        )
                )
                .role(registerRequestDto.getRole())
                .build();

        // Save the new user in the database
        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {

        // Normalize email in the same way as registration
        String normalizedEmail = loginRequestDto
                .getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        UserDetails userDetails;

        try {
            // Authenticate email and password using Spring Security
            userDetails = (UserDetails) authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    normalizedEmail,
                                    loginRequestDto.getPassword()
                            )
                    )
                    .getPrincipal();

        } catch (AuthenticationException exception) {

            // Return one generic message for invalid email or password
            throw new org.springframework.security
                    .authentication.BadCredentialsException(
                    AuthConstants.MESSAGE_401
            );
        }

        // Get user details required for the login response
        User user = userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new org.springframework.security
                                .authentication.BadCredentialsException(
                                AuthConstants.MESSAGE_401
                        )
                );

        // Generate JWT only after successful authentication
        String accessToken =
                jwtService.generateToken(userDetails);

        // Return token and basic logged-in user details to frontend
        return AuthResponseDto.builder()
                .accessToken(accessToken)
                .tokenType("Bearer")
                .expiresIn(jwtService.getExpirationInSeconds())
                .userId(user.getId())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole())
                .build();
    }
}
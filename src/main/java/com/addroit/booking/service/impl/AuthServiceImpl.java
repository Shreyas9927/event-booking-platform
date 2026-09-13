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

        String normalizedEmail = registerRequestDto
                .getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        if (userRepository.existsByEmail(normalizedEmail)) {
            throw new EmailAlreadyExistsException(
                    AuthConstants.MESSAGE_409
            );
        }

        User user = User.builder()
                .name(registerRequestDto.getName().trim())
                .email(normalizedEmail)
                .passwordHash(
                        passwordEncoder.encode(
                                registerRequestDto.getPassword()
                        )
                )
                .role(registerRequestDto.getRole())
                .build();

        userRepository.save(user);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResponseDto login(LoginRequestDto loginRequestDto) {

        String normalizedEmail = loginRequestDto
                .getEmail()
                .trim()
                .toLowerCase(Locale.ROOT);

        UserDetails userDetails;

        try {
            userDetails = (UserDetails) authenticationManager
                    .authenticate(
                            new UsernamePasswordAuthenticationToken(
                                    normalizedEmail,
                                    loginRequestDto.getPassword()
                            )
                    )
                    .getPrincipal();

        } catch (AuthenticationException exception) {
            throw new org.springframework.security
                    .authentication.BadCredentialsException(
                    AuthConstants.MESSAGE_401
            );
        }

        User user = userRepository
                .findByEmail(normalizedEmail)
                .orElseThrow(() ->
                        new org.springframework.security
                                .authentication.BadCredentialsException(
                                AuthConstants.MESSAGE_401
                        )
                );

        String accessToken =
                jwtService.generateToken(userDetails);

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
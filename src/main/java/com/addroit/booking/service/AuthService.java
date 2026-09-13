package com.addroit.booking.service;

import com.addroit.booking.dto.auth.AuthResponseDto;
import com.addroit.booking.dto.auth.LoginRequestDto;
import com.addroit.booking.dto.auth.RegisterRequestDto;
import jakarta.validation.Valid;

public interface AuthService {
    void register(@Valid RegisterRequestDto registerRequestDto);

    AuthResponseDto login(@Valid LoginRequestDto loginRequestDto);
}

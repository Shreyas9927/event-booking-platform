package com.addroit.booking.dto.auth;

import com.addroit.booking.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponseDto {

    private String accessToken;

    private String tokenType;

    private Long expiresIn;

    private Long userId;

    private String name;

    private String email;

    private Role role;
}
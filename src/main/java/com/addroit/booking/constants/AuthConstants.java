package com.addroit.booking.constants;

public final class AuthConstants {

    private AuthConstants() {
    }

    // Registration
    public static final String STATUS_201 = "201";
    public static final String MESSAGE_201 =
            "User registered successfully";

    // Login
    public static final String STATUS_200 = "200";
    public static final String MESSAGE_200 =
            "Login successful";

    // Bad request
    public static final String STATUS_400 = "400";
    public static final String MESSAGE_400 =
            "Invalid request data";

    // Unauthorized
    public static final String STATUS_401 = "401";
    public static final String MESSAGE_401 =
            "Invalid email or password";

    public static final String STATUS_403 = "403";
    public static final String MESSAGE_403 =
            "You do not have permission to perform this operation";

    public static final String STATUS_404 = "404";
    public static final String MESSAGE_404 = "User not found";

    // Conflict
    public static final String STATUS_409 = "409";
    public static final String MESSAGE_409 =
            "An account already exists with this email";

    public static final String MESSAGE_INVALID_TOKEN =
            "Invalid or expired authentication token";
}
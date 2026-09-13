package com.addroit.booking.exception;

import com.addroit.booking.constants.AuthConstants;
import com.addroit.booking.constants.BookingConstants;
import com.addroit.booking.constants.EventConstants;
import com.addroit.booking.dto.ErrorResponseDto;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleUserNotFoundException(
            UserNotFoundException exception,
            HttpServletRequest request) {

        return buildErrorResponse(
                request,
                AuthConstants.STATUS_404,
                exception.getMessage(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponseDto> handleBadCredentialsException(
            BadCredentialsException exception,
            HttpServletRequest request) {

        return buildErrorResponse(
                request,
                AuthConstants.STATUS_401,
                AuthConstants.MESSAGE_401,
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDto> handleAccessDeniedException(
            AccessDeniedException exception,
            HttpServletRequest request) {

        return buildErrorResponse(
                request,
                AuthConstants.STATUS_403,
                AuthConstants.MESSAGE_403,
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ErrorResponseDto> handleEmailAlreadyExistsException(
            EmailAlreadyExistsException exception,
            HttpServletRequest request) {

        return buildErrorResponse(
                request,
                AuthConstants.STATUS_409,
                exception.getMessage(),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleEventNotFoundException(
            EventNotFoundException exception,
            HttpServletRequest request) {

        return buildErrorResponse(
                request,
                EventConstants.STATUS_404,
                exception.getMessage(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(BookingNotFoundException.class)
    public ResponseEntity<ErrorResponseDto> handleBookingNotFoundException(
            BookingNotFoundException exception,
            HttpServletRequest request) {

        return buildErrorResponse(
                request,
                BookingConstants.STATUS_404,
                exception.getMessage(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler({
            InsufficientTicketsException.class,
            BookingAlreadyCancelledException.class
    })
    public ResponseEntity<ErrorResponseDto> handleBookingConflict(
            RuntimeException exception,
            HttpServletRequest request) {

        return buildErrorResponse(
                request,
                BookingConstants.STATUS_409,
                exception.getMessage(),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(ForbiddenOperationException.class)
    public ResponseEntity<ErrorResponseDto> handleForbiddenOperationException(
            ForbiddenOperationException exception,
            HttpServletRequest request) {

        return buildErrorResponse(
                request,
                BookingConstants.STATUS_403,
                exception.getMessage(),
                HttpStatus.FORBIDDEN
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleValidationException(
            MethodArgumentNotValidException exception,
            HttpServletRequest request) {

        String validationMessage = exception
                .getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error ->
                        error.getField()
                                + ": "
                                + error.getDefaultMessage()
                )
                .collect(Collectors.joining(", "));

        return buildErrorResponse(
                request,
                AuthConstants.STATUS_400,
                validationMessage,
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ErrorResponseDto> handleJwtException(
            JwtException exception,
            HttpServletRequest request) {

        return buildErrorResponse(
                request,
                AuthConstants.STATUS_401,
                AuthConstants.MESSAGE_INVALID_TOKEN,
                HttpStatus.UNAUTHORIZED
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDto> handleGenericException(
            Exception exception,
            HttpServletRequest request) {

        return buildErrorResponse(
                request,
                "500",
                "An unexpected error occurred",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    private ResponseEntity<ErrorResponseDto> buildErrorResponse(
            HttpServletRequest request,
            String errorCode,
            String errorMessage,
            HttpStatus httpStatus) {

        ErrorResponseDto errorResponseDto = new ErrorResponseDto(
                request.getRequestURI(),
                errorCode,
                errorMessage,
                LocalDateTime.now()
        );

        return ResponseEntity
                .status(httpStatus)
                .body(errorResponseDto);
    }
}
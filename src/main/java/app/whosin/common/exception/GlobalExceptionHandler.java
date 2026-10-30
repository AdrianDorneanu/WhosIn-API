package app.whosin.common.exception;

import app.whosin.auth.exception.EmailAlreadyExistsException;
import app.whosin.auth.exception.InvalidCredentialsException;
import app.whosin.auth.exception.InvalidRefreshTokenException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotFound(
            ResourceNotFoundException exception,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.NOT_FOUND,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleForbidden(
            AccessDeniedException exception,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.FORBIDDEN,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler({
            InvalidCredentialsException.class,
            InvalidRefreshTokenException.class
    })
    public ResponseEntity<ApiError> handleUnauthorized(
            RuntimeException exception,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.UNAUTHORIZED,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiError> handleConflict(
            EmailAlreadyExistsException exception,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.CONFLICT,
                exception.getMessage(),
                request.getRequestURI()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(
            MethodArgumentNotValidException exception,
            HttpServletRequest request
    ) {
        Map<String, String> validationErrors = new LinkedHashMap<>();

        exception.getBindingResult()
                .getFieldErrors()
                .forEach(error ->
                        validationErrors.put(
                                error.getField(),
                                error.getDefaultMessage()
                        )
                );

        ApiError body = new ApiError(
                Instant.now(),
                HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),
                "Validation failed",
                request.getRequestURI(),
                validationErrors
        );

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleUnexpectedException(
            Exception exception,
            HttpServletRequest request
    ) {
        return buildError(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred",
                request.getRequestURI()
        );
    }

    @ExceptionHandler(FeatureNotImplementedException.class)
    public ResponseEntity<ApiError> handleFeatureNotImplemented(
            FeatureNotImplementedException exception,
            HttpServletRequest request
    ) {
        HttpStatus status = HttpStatus.NOT_IMPLEMENTED;

        ApiError error = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI(),
                null
        );

        return ResponseEntity.status(status).body(error);
    }

    private ResponseEntity<ApiError> buildError(
            HttpStatus status,
            String message,
            String path
    ) {
        ApiError body = new ApiError(
                Instant.now(),
                status.value(),
                status.getReasonPhrase(),
                message,
                path,
                null
        );

        return ResponseEntity.status(status).body(body);
    }
}
package app.whosin.common.exception;

import app.whosin.auth.exception.EmailAlreadyExistsException;
import app.whosin.auth.exception.InvalidCredentialsException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class GlobalExceptionHandlerTests {
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(new ErrorController())
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    @Test
    void missingGameReturnsNotFound() throws Exception {
        assertError("game", 404, "Not Found", "Game not found");
    }

    @Test
    void missingUserReturnsNotFound() throws Exception {
        assertError("user", 404, "Not Found", "User not found");
    }

    @Test
    void accessDeniedReturnsForbidden() throws Exception {
        assertError("forbidden", 403, "Forbidden", "Only active users can create games");
    }

    @Test
    void invalidCredentialsStillReturnUnauthorized() throws Exception {
        assertError("credentials", 401, "Unauthorized", "Invalid credentials");
    }

    @Test
    void duplicateEmailStillReturnsConflict() throws Exception {
        assertError("conflict", 409, "Conflict", "Email already exists");
    }

    @Test
    void unexpectedErrorsDoNotExposeInternalDetails() throws Exception {
        assertError("unexpected", 500, "Internal Server Error", "An unexpected error occurred");
    }

    private void assertError(String kind, int statusCode, String error, String message) throws Exception {
        mockMvc.perform(get("/test-errors/" + kind))
                .andExpect(status().is(statusCode))
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.timestamp").isNotEmpty())
                .andExpect(jsonPath("$.status").value(statusCode))
                .andExpect(jsonPath("$.error").value(error))
                .andExpect(jsonPath("$.message").value(message))
                .andExpect(jsonPath("$.path").value("/test-errors/" + kind))
                .andExpect(jsonPath("$.validationErrors").value(nullValue()));
    }

    @RestController
    static class ErrorController {
        @GetMapping("/test-errors/{kind}")
        public void fail(@PathVariable("kind") String kind) {
            throw switch (kind) {
                case "game" -> new ResourceNotFoundException("Game not found");
                case "user" -> new ResourceNotFoundException("User not found");
                case "forbidden" -> new AccessDeniedException("Only active users can create games");
                case "credentials" -> new InvalidCredentialsException("Invalid credentials");
                case "conflict" -> new EmailAlreadyExistsException("Email already exists");
                default -> new RuntimeException("Internal database details");
            };
        }
    }
}

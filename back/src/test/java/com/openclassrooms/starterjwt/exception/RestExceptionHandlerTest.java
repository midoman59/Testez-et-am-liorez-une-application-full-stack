package com.openclassrooms.starterjwt.exception;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import static org.junit.jupiter.api.Assertions.assertEquals;

class RestExceptionHandlerTest {

    private final RestExceptionHandler handler = new RestExceptionHandler();

    @Test
    void handleBadRequestExceptions_shouldReturnBadRequest() {
        assertEquals(HttpStatus.BAD_REQUEST, handler.handleBadRequestExceptions().getStatusCode());
    }

    @Test
    void handleBadRequestException_shouldReturnBadRequest() {
        assertEquals(HttpStatus.BAD_REQUEST, handler.handleBadRequestException().getStatusCode());
    }

    @Test
    void handleNotFoundException_shouldReturnNotFound() {
        assertEquals(HttpStatus.NOT_FOUND, handler.handleNotFoundException().getStatusCode());
    }

    @Test
    void handleUnauthorizedException_shouldReturnUnauthorized() {
        assertEquals(HttpStatus.UNAUTHORIZED, handler.handleUnauthorizedException().getStatusCode());
    }
}

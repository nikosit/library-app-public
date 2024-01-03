package de.schwarz.libraryapp.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpClientErrorException;

import java.time.format.DateTimeParseException;

@Slf4j
@RestControllerAdvice
public class BaseExceptionHandler {

    @ExceptionHandler(value = {NoContentException.class})
    public ResponseEntity<String> handleNoContentException(NoContentException e) {
        log.error("NoContent Exception: {}", e.getMessage());
        return ResponseEntity
                .noContent()
                .build();
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    protected ResponseEntity<?> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error("IllegalArgument Exception: {}", e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }

    @ExceptionHandler(value = DateTimeParseException.class)
    protected ResponseEntity<?> handleDateTimeParseException(DateTimeParseException e) {
        log.error("DateTimeParse Exception: {}", e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }

    @ExceptionHandler(value = {HttpClientErrorException.BadRequest.class})
    public ResponseEntity<String> handleBadRequestException(HttpClientErrorException.BadRequest e) {
        log.error("BadRequest Exception: {}", e.getMessage());
        return ResponseEntity
                .badRequest()
                .body(e.getMessage());
    }

    @ExceptionHandler(value = {HttpClientErrorException.Unauthorized.class})
    public ResponseEntity<String> handleUnauthorizedException(HttpClientErrorException.Unauthorized e) {
        log.error("Unauthorized Exception: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(e.getMessage());
    }

    @ExceptionHandler(value = {HttpClientErrorException.Forbidden.class})
    public ResponseEntity<String> handleForbiddenException(HttpClientErrorException.Forbidden e) {
        log.error("Forbidden Exception: {}", e.getMessage());
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(e.getMessage());
    }

    @ExceptionHandler(value = {InternalError.class})
    public ResponseEntity<String> handleInternalError(InternalError error) {
        log.error("Internal Error: {}", error.getMessage());
        return ResponseEntity
                .internalServerError()
                .body(error.getMessage());
    }
}

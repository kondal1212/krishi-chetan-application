package com.krishichetan.exception;

import com.krishichetan.dto.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.time.LocalDateTime;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Handles the most common issue in multi-modal apps:
     * The user uploads a file that is too large (e.g., a 20MB 4K photo).
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxSizeException(MaxUploadSizeExceededException exc) {
        log.warn("Payload too large: {}", exc.getMessage());
        ErrorResponse error = new ErrorResponse(
                "The image or audio file is too large. Please keep it under 5MB.",
                "FILE_TOO_LARGE",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(error);
    }

    /**
     * Catch-all for any unhandled logic errors (API timeouts, NullPointers, etc.)
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneralException(Exception exc) {
        log.error("Unhandled Exception: ", exc); // Log the full stack trace for the developer

        ErrorResponse error = new ErrorResponse(
                "An unexpected error occurred in the Krishi-Chetan pipeline. Please try again later.",
                "INTERNAL_SERVER_ERROR",
                LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }
}
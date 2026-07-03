package br.com.trcon.site.shared.exception;

import java.time.Instant;
import java.util.Map;

public record ErrorResponse(
        String code,
        String message,
        Instant timestamp,
        String path,
        Map<String, String> fields
) {

    public static ErrorResponse of(String code, String message, String path) {
        return new ErrorResponse(code, message, Instant.now(), path, null);
    }

    public static ErrorResponse ofValidation(String message, String path, Map<String, String> fields) {
        return new ErrorResponse("VALIDATION_ERROR", message, Instant.now(), path, fields);
    }
}

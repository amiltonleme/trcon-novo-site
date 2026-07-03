package br.com.trcon.site.shared.exception;

import org.springframework.http.HttpStatus;

public abstract class ApiException extends RuntimeException {

    private final String code;
    private final HttpStatus httpStatus;

    protected ApiException(String code, HttpStatus httpStatus, String message) {
        super(message);
        this.code = code;
        this.httpStatus = httpStatus;
    }

    public String getCode() {
        return code;
    }

    public HttpStatus getHttpStatus() {
        return httpStatus;
    }
}

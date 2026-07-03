package br.com.trcon.site.news.domain;

import br.com.trcon.site.shared.exception.ApiException;
import org.springframework.http.HttpStatus;

public class NewsQueryInvalidaException extends ApiException {

    public NewsQueryInvalidaException(String message) {
        super("VALIDATION_ERROR", HttpStatus.BAD_REQUEST, message);
    }
}

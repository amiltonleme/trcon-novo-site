package br.com.trcon.site.chat.exception;

import br.com.trcon.site.shared.exception.ApiException;
import org.springframework.http.HttpStatus;

public class ChatProviderUnavailableException extends ApiException {
    public ChatProviderUnavailableException() { super("AI_PROVIDER_UNAVAILABLE", HttpStatus.SERVICE_UNAVAILABLE, "Assistente temporariamente indisponível."); }
}

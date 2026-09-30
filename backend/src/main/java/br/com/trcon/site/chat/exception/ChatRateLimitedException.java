package br.com.trcon.site.chat.exception;

import br.com.trcon.site.shared.exception.ApiException;
import org.springframework.http.HttpStatus;

public class ChatRateLimitedException extends ApiException {
    public ChatRateLimitedException() { super("CHAT_RATE_LIMITED", HttpStatus.TOO_MANY_REQUESTS, "Muitas mensagens em pouco tempo. Tente novamente em instantes."); }
}

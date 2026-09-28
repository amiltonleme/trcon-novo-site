package br.com.trcon.site.chat.exception;

import br.com.trcon.site.shared.exception.ApiException;
import org.springframework.http.HttpStatus;

public class ChatBudgetExceededException extends ApiException {
    public ChatBudgetExceededException() { super("CHAT_BUDGET_EXCEEDED", HttpStatus.TOO_MANY_REQUESTS, "Assistente temporariamente indisponível."); }
}

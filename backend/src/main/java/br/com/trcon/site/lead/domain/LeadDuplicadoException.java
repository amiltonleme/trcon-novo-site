package br.com.trcon.site.lead.domain;

import br.com.trcon.site.shared.exception.ApiException;
import org.springframework.http.HttpStatus;

public class LeadDuplicadoException extends ApiException {

    public LeadDuplicadoException(String email, String origem) {
        super("LEAD_DUPLICADO", HttpStatus.CONFLICT,
                "Já existe um cadastro para o email '%s' nesta origem '%s'.".formatted(email, origem));
    }
}

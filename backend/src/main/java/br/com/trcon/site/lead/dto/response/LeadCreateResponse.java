package br.com.trcon.site.lead.dto.response;

import br.com.trcon.site.lead.domain.LeadStatus;

import java.util.UUID;

public record LeadCreateResponse(
        UUID id,
        LeadStatus status,
        String message
) {

    public static LeadCreateResponse criado(UUID id, LeadStatus status) {
        return new LeadCreateResponse(id, status, "Cadastro recebido com sucesso.");
    }
}

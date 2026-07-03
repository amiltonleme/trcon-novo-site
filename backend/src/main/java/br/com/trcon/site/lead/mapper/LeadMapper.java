package br.com.trcon.site.lead.mapper;

import br.com.trcon.site.lead.domain.Lead;
import br.com.trcon.site.lead.dto.request.LeadCreateRequest;
import br.com.trcon.site.lead.dto.response.LeadCreateResponse;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LeadMapper {

    default Lead toDomain(LeadCreateRequest request, String emailNormalizado) {
        return Lead.novo(
                request.nome(),
                emailNormalizado,
                request.telefone(),
                request.tipoInteresse(),
                request.mensagem(),
                request.origem(),
                request.consentimentoLgpd()
        );
    }

    default LeadCreateResponse toResponse(Lead lead) {
        return LeadCreateResponse.criado(lead.getId(), lead.getStatus());
    }
}

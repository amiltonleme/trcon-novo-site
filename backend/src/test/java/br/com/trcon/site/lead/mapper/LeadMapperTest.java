package br.com.trcon.site.lead.mapper;

import br.com.trcon.site.lead.domain.Lead;
import br.com.trcon.site.lead.domain.LeadStatus;
import br.com.trcon.site.lead.domain.LeadType;
import br.com.trcon.site.lead.dto.request.LeadCreateRequest;
import br.com.trcon.site.lead.dto.response.LeadCreateResponse;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LeadMapperTest {

    private final LeadMapper leadMapper = new LeadMapperImpl();

    @Test
    void deveMapearRequestParaDomain() {
        LeadCreateRequest request = new LeadCreateRequest(
                "Fulano da Silva",
                "fulano@empresa.com",
                "+55 11 99999-9999",
                LeadType.CUSTOMIZACAO,
                "Quero customizar meu sistema atual.",
                "site-trcon-servicos",
                true
        );

        Lead lead = leadMapper.toDomain(request, "fulano@empresa.com");

        assertThat(lead.getNome()).isEqualTo("Fulano da Silva");
        assertThat(lead.getEmail()).isEqualTo("fulano@empresa.com");
        assertThat(lead.getTelefone()).isEqualTo("+55 11 99999-9999");
        assertThat(lead.getTipoInteresse()).isEqualTo(LeadType.CUSTOMIZACAO);
        assertThat(lead.getMensagem()).isEqualTo("Quero customizar meu sistema atual.");
        assertThat(lead.getOrigem()).isEqualTo("site-trcon-servicos");
        assertThat(lead.isConsentimentoLgpd()).isTrue();
        assertThat(lead.getStatus()).isEqualTo(LeadStatus.PENDING);
    }

    @Test
    void deveMapearRequestSemMensagemOpcional() {
        LeadCreateRequest request = new LeadCreateRequest(
                "Ciclana",
                "ciclana@empresa.com",
                "+55 11 98888-8888",
                LeadType.PRODUTO,
                null,
                "site-trcon-home",
                true
        );

        Lead lead = leadMapper.toDomain(request, "ciclana@empresa.com");

        assertThat(lead.getMensagem()).isNull();
    }

    @Test
    void deveMapearDomainParaResponse() {
        Lead lead = Lead.novo("Fulano", "fulano@empresa.com", "999999999",
                LeadType.DESENVOLVIMENTO_SOB_DEMANDA, "msg", "origem", true);

        LeadCreateResponse response = leadMapper.toResponse(lead);

        assertThat(response.id()).isEqualTo(lead.getId());
        assertThat(response.status()).isEqualTo(LeadStatus.PENDING);
        assertThat(response.message()).isEqualTo("Cadastro recebido com sucesso.");
    }
}

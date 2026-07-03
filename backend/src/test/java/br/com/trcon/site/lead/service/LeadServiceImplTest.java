package br.com.trcon.site.lead.service;

import br.com.trcon.site.lead.domain.Lead;
import br.com.trcon.site.lead.domain.LeadDuplicadoException;
import br.com.trcon.site.lead.domain.LeadStatus;
import br.com.trcon.site.lead.domain.LeadType;
import br.com.trcon.site.lead.dto.request.LeadCreateRequest;
import br.com.trcon.site.lead.dto.response.LeadCreateResponse;
import br.com.trcon.site.lead.mapper.LeadMapper;
import br.com.trcon.site.lead.repository.LeadRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeadServiceImplTest {

    @Mock
    private LeadRepository leadRepository;

    @Mock
    private LeadMapper leadMapper;

    @InjectMocks
    private LeadServiceImpl leadService;

    private LeadCreateRequest request;

    @BeforeEach
    void setUp() {
        request = new LeadCreateRequest(
                "Fulano da Silva",
                "Fulano@Empresa.com",
                "+55 11 99999-9999",
                LeadType.ALOCACAO_MAO_DE_OBRA,
                "Preciso de 2 devs sêniores.",
                "site-trcon-servicos",
                true
        );
    }

    @Test
    void deveCriarLeadQuandoNaoExisteDuplicidade() {
        Lead leadSalvo = Lead.novo("Fulano da Silva", "fulano@empresa.com", "+55 11 99999-9999",
                LeadType.ALOCACAO_MAO_DE_OBRA, "msg", "site-trcon-servicos", true);
        LeadCreateResponse responseEsperada = LeadCreateResponse.criado(leadSalvo.getId(), LeadStatus.PENDING);

        when(leadRepository.existsByEmailAndOrigem("fulano@empresa.com", "site-trcon-servicos"))
                .thenReturn(false);
        when(leadMapper.toDomain(any(), anyString())).thenReturn(leadSalvo);
        when(leadRepository.save(leadSalvo)).thenReturn(leadSalvo);
        when(leadMapper.toResponse(leadSalvo)).thenReturn(responseEsperada);

        LeadCreateResponse resultado = leadService.criar(request);

        assertThat(resultado).isEqualTo(responseEsperada);
        verify(leadRepository).save(leadSalvo);
    }

    @Test
    void deveNormalizarEmailAntesDeVerificarDuplicidade() {
        Lead leadSalvo = Lead.novo("Fulano da Silva", "fulano@empresa.com", "+55 11 99999-9999",
                LeadType.ALOCACAO_MAO_DE_OBRA, "msg", "site-trcon-servicos", true);
        when(leadRepository.existsByEmailAndOrigem("fulano@empresa.com", "site-trcon-servicos"))
                .thenReturn(false);
        when(leadMapper.toDomain(any(), anyString())).thenReturn(leadSalvo);
        when(leadRepository.save(leadSalvo)).thenReturn(leadSalvo);
        when(leadMapper.toResponse(leadSalvo)).thenReturn(
                LeadCreateResponse.criado(leadSalvo.getId(), LeadStatus.PENDING));

        leadService.criar(request);

        verify(leadRepository).existsByEmailAndOrigem("fulano@empresa.com", "site-trcon-servicos");
    }

    @Test
    void deveLancarExcecaoQuandoLeadDuplicado() {
        when(leadRepository.existsByEmailAndOrigem("fulano@empresa.com", "site-trcon-servicos"))
                .thenReturn(true);

        assertThatThrownBy(() -> leadService.criar(request))
                .isInstanceOf(LeadDuplicadoException.class);

        verify(leadRepository, never()).save(any());
    }
}

package br.com.trcon.site.chat.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import br.com.trcon.site.chat.domain.ChatUsageLog;
import br.com.trcon.site.chat.exception.ChatBudgetExceededException;
import br.com.trcon.site.chat.repository.ChatUsageLogRepository;
import br.com.trcon.site.shared.config.ChatAiProperties;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

class ChatQuotaServiceTest {
    private final ChatUsageLogRepository repository = Mockito.mock(ChatUsageLogRepository.class);
    private final ChatAiProperties properties = new ChatAiProperties(true, false, "key", "https://api.deepseek.com",
            "deepseek-flash", 400, 6, 8, 10, 0.30, 1.20);
    private final ChatQuotaService service = new ChatQuotaService(repository, properties);

    @Test
    void bloqueiaAntesDaChamadaQuandoOrcamentoAtingiuLimite() {
        when(repository.sumCostSince(any())).thenReturn(new BigDecimal("10.00000000"));

        assertThatThrownBy(service::assertWithinBudget).isInstanceOf(ChatBudgetExceededException.class);
    }

    @Test
    void permiteQuandoAindaNaoHaUsoOuEstaAbaixoDoLimite() {
        when(repository.sumCostSince(any())).thenReturn(null, new BigDecimal("9.99999999"));

        service.assertWithinBudget();
        service.assertWithinBudget();
    }

    @Test
    void calculaEPersisteCustoSemConteudoDaConversa() {
        service.log(1_000_000, 1_000_000, "hash-do-cliente");

        ArgumentCaptor<ChatUsageLog> captor = ArgumentCaptor.forClass(ChatUsageLog.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getEstimatedCostUsd()).isEqualByComparingTo("1.50000000");
        assertThat(captor.getValue().getClientHash()).isEqualTo("hash-do-cliente");
    }
}

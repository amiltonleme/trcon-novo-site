package br.com.trcon.site.chat.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;
import org.junit.jupiter.api.Test;

class ChatKnowledgeProviderTest {
    private final ChatKnowledgeProvider provider = new ChatKnowledgeProvider();

    @Test
    void reconheceSomenteIdsVersionados() {
        assertThat(provider.containsAll(List.of("company.identity", "business.offerings", "products.hub"))).isTrue();
        assertThat(provider.containsAll(List.of("cliente.inventado"))).isFalse();
    }

    @Test
    void baseRegistraEstadoComercialSemSugerirSigilo() {
        assertThat(provider.content()).contains("não possui contratos ativos");
        assertThat(provider.content()).doesNotContain("clientes confidenciais existentes");
    }

    @Test
    void baseRegistraGovernancaEstadosDeProdutoEFallback() {
        assertThat(provider.content())
                .contains("Direção da TRCONGROUP", "Responsável técnico do site")
                .contains("products.hub", "produto próprio em beta")
                .contains("products.scheduling", "produto próprio em desenvolvimento")
                .contains("products.marketing", "produto próprio em desenvolvimento")
                .contains("governance.missing")
                .doesNotContain("fundada em 2005");
    }
}

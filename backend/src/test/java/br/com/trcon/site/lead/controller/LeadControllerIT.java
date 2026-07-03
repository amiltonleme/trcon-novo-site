package br.com.trcon.site.lead.controller;

import br.com.trcon.site.TestcontainersConfiguration;
import br.com.trcon.site.shared.exception.ErrorResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
class LeadControllerIT {

    @Autowired
    private TestRestTemplate restTemplate;

    @Test
    void devePersistirLeadEResponder201() {
        Map<String, Object> payload = requestValido();

        ResponseEntity<Map> response = restTemplate.postForEntity("/api/v1/site/leads", payload, Map.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody()).containsEntry("status", "PENDING");
        assertThat(response.getBody()).containsKey("id");
    }

    @Test
    void deveResponder409QuandoEmailDuplicadoNaMesmaOrigem() {
        Map<String, Object> payload = requestValido();
        String origem = "origem-duplicada-" + UUID.randomUUID();
        payload.put("origem", origem);

        restTemplate.postForEntity("/api/v1/site/leads", payload, Map.class);
        ResponseEntity<ErrorResponse> segunda =
                restTemplate.postForEntity("/api/v1/site/leads", payload, ErrorResponse.class);

        assertThat(segunda.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(segunda.getBody()).isNotNull();
        assertThat(segunda.getBody().code()).isEqualTo("LEAD_DUPLICADO");
    }

    @Test
    void deveResponder400QuandoPayloadInvalido() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("nome", "");
        payload.put("email", "email-invalido");
        payload.put("telefone", "");
        payload.put("origem", "site-trcon");
        payload.put("consentimentoLgpd", false);

        ResponseEntity<ErrorResponse> response =
                restTemplate.postForEntity("/api/v1/site/leads", payload, ErrorResponse.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody()).isNotNull();
        assertThat(response.getBody().code()).isEqualTo("VALIDATION_ERROR");
        assertThat(response.getBody().fields()).containsKeys("email", "consentimentoLgpd");
    }

    private Map<String, Object> requestValido() {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("nome", "Fulano da Silva");
        payload.put("email", "fulano-" + UUID.randomUUID() + "@empresa.com");
        payload.put("telefone", "+55 11 99999-9999");
        payload.put("tipoInteresse", "ALOCACAO_MAO_DE_OBRA");
        payload.put("mensagem", "Preciso de 2 devs sêniores para squad de 3 meses.");
        payload.put("origem", "site-trcon-servicos");
        payload.put("consentimentoLgpd", true);
        return payload;
    }
}

package br.com.trcon.site.lead.integration;

import br.com.trcon.site.shared.config.MailProperties;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class ResendEmailClientTest {

    private HttpServer server;
    private String baseUrl;
    private final AtomicReference<String> lastBody = new AtomicReference<>();
    private final AtomicReference<String> lastAuth = new AtomicReference<>();

    @BeforeEach
    void setUp() throws IOException {
        lastBody.set(null);
        lastAuth.set(null);
        server = HttpServer.create(new InetSocketAddress("127.0.0.1", 0), 0);
        server.createContext("/emails", exchange -> {
            lastAuth.set(exchange.getRequestHeaders().getFirst("Authorization"));
            lastBody.set(new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
            byte[] ok = "{}".getBytes(StandardCharsets.UTF_8);
            exchange.getResponseHeaders().add("Content-Type", "application/json");
            exchange.sendResponseHeaders(200, ok.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(ok);
            }
        });
        server.start();
        baseUrl = "http://127.0.0.1:" + server.getAddress().getPort();
    }

    @AfterEach
    void tearDown() {
        server.stop(0);
    }

    @Test
    void rejeitaQuandoMailNaoConfigurado() {
        MailProperties props = new MailProperties(false, "", "", "");
        ResendEmailClient client = new ResendEmailClient(props, RestClient.builder().baseUrl(baseUrl).build());

        assertThatThrownBy(() -> client.sendHtml("a@b.com", "s", "<p>x</p>", null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Resend nao configurado");
    }

    @Test
    void enviaPayloadComReplyTo() {
        MailProperties props =
                new MailProperties(true, "re_test", "Site <noreply@trcongroup.com.br>", "amilton.leme@trcongroup.com.br");
        ResendEmailClient client = new ResendEmailClient(props, RestClient.builder().baseUrl(baseUrl).build());

        client.sendHtml("amilton.leme@trcongroup.com.br", "Assunto", "<p>ola</p>", "lead@ex.com");

        assertThat(lastAuth.get()).isEqualTo("Bearer re_test");
        assertThat(lastBody.get())
                .contains("\"from\":\"Site <noreply@trcongroup.com.br>\"")
                .contains("\"reply_to\":\"lead@ex.com\"")
                .contains("\"subject\":\"Assunto\"")
                .contains("\"html\":\"<p>ola</p>\"");
    }

    @Test
    void omiteReplyToQuandoNulo() {
        MailProperties props =
                new MailProperties(true, "re_test", "noreply@trcongroup.com.br", "amilton.leme@trcongroup.com.br");
        ResendEmailClient client = new ResendEmailClient(props, RestClient.builder().baseUrl(baseUrl).build());

        client.sendHtml("amilton.leme@trcongroup.com.br", "Assunto", "<p>ola</p>", null);

        assertThat(lastBody.get()).doesNotContain("reply_to");
    }

    @Test
    void omiteReplyToQuandoEmBranco() {
        MailProperties props =
                new MailProperties(true, "re_test", "noreply@trcongroup.com.br", "amilton.leme@trcongroup.com.br");
        ResendEmailClient client = new ResendEmailClient(props, RestClient.builder().baseUrl(baseUrl).build());

        client.sendHtml("amilton.leme@trcongroup.com.br", "Assunto", "<p>ola</p>", "  ");

        assertThat(lastBody.get()).doesNotContain("reply_to");
    }
}

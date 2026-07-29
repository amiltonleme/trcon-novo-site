package br.com.trcon.site.lead.integration;

import br.com.trcon.site.shared.config.MailProperties;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Component;
import org.springframework.http.MediaType;
import org.springframework.web.client.RestClient;

@Component
public class ResendEmailClient {

    private final MailProperties mailProperties;
    private final RestClient restClient;

    public ResendEmailClient(MailProperties mailProperties) {
        this.mailProperties = mailProperties;
        this.restClient = RestClient.builder().baseUrl("https://api.resend.com").build();
    }

    public void sendHtml(String to, String subject, String html, String replyTo) {
        if (!mailProperties.isConfigured()) {
            throw new IllegalStateException(
                    "Resend nao configurado (TRCON_SITE_MAIL_ENABLED / API_KEY / FROM / NOTIFY_TO)");
        }

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("from", mailProperties.from());
        body.put("to", List.of(to));
        body.put("subject", subject);
        body.put("html", html);
        if (replyTo != null && !replyTo.isBlank()) {
            body.put("reply_to", replyTo);
        }

        restClient
                .post()
                .uri("/emails")
                .contentType(MediaType.APPLICATION_JSON)
                .header("Authorization", "Bearer " + mailProperties.apiKey())
                .body(body)
                .retrieve()
                .toBodilessEntity();
    }
}

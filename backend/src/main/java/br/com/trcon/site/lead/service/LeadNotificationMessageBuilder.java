package br.com.trcon.site.lead.service;

import br.com.trcon.site.lead.domain.Lead;
import br.com.trcon.site.shared.util.EmailTextSanitizer;

/** Monta subject/html da notificação de lead (fora do service de orquestração). */
public final class LeadNotificationMessageBuilder {

    private LeadNotificationMessageBuilder() {}

    public static String subject(Lead lead) {
        return EmailTextSanitizer.header(
                "[TRCONGROUP Site] Novo lead — " + lead.getTipoInteresse() + " (" + lead.getOrigem() + ")");
    }

    public static String html(Lead lead) {
        String mensagem = lead.getMensagem() == null || lead.getMensagem().isBlank()
                ? "—"
                : EmailTextSanitizer.html(lead.getMensagem()).replace("\n", "<br>");

        return """
                <h2>Novo lead no site TRCONGROUP</h2>
                <p><strong>ID:</strong> %s</p>
                <p><strong>Nome:</strong> %s</p>
                <p><strong>E-mail:</strong> %s</p>
                <p><strong>Telefone:</strong> %s</p>
                <p><strong>Interesse:</strong> %s</p>
                <p><strong>Origem:</strong> %s</p>
                <p><strong>Mensagem:</strong><br>%s</p>
                <p style="color:#666;font-size:12px;">Responda este e-mail para falar com o lead (Reply-To).</p>
                """
                .formatted(
                        EmailTextSanitizer.html(String.valueOf(lead.getId())),
                        EmailTextSanitizer.html(lead.getNome()),
                        EmailTextSanitizer.html(lead.getEmail()),
                        EmailTextSanitizer.html(lead.getTelefone()),
                        EmailTextSanitizer.html(String.valueOf(lead.getTipoInteresse())),
                        EmailTextSanitizer.html(lead.getOrigem()),
                        mensagem);
    }
}

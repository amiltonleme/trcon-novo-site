package br.com.trcon.site.lead.service;

import br.com.trcon.site.lead.domain.Lead;
import br.com.trcon.site.lead.integration.ResendEmailClient;
import br.com.trcon.site.shared.config.MailProperties;
import br.com.trcon.site.shared.util.EmailTextSanitizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Notifica a TRCon por e-mail quando um lead é criado.
 * Falhas de envio são absorvidas — o lead já foi persistido.
 */
@Service
public class LeadEmailNotifier implements LeadNotifier {

    private static final Logger log = LoggerFactory.getLogger(LeadEmailNotifier.class);

    private final MailProperties mailProperties;
    private final ResendEmailClient resendEmailClient;

    public LeadEmailNotifier(MailProperties mailProperties, ResendEmailClient resendEmailClient) {
        this.mailProperties = mailProperties;
        this.resendEmailClient = resendEmailClient;
    }

    @Override
    public void notifyNewLead(Lead lead) {
        if (!mailProperties.isConfigured()) {
            log.info("Notificacao de lead desabilitada ou incompleta. leadId={}", lead.getId());
            return;
        }

        try {
            String subject = EmailTextSanitizer.header(
                    "[TRCon Site] Novo lead — " + lead.getTipoInteresse() + " (" + lead.getOrigem() + ")");
            String html = buildHtml(lead);
            String replyTo = EmailTextSanitizer.header(lead.getEmail());

            resendEmailClient.sendHtml(mailProperties.notifyTo(), subject, html, replyTo);
            log.info("Notificacao de lead enviada. leadId={} to={}", lead.getId(), mailProperties.notifyTo());
        } catch (RuntimeException ex) {
            log.error(
                    "Falha ao notificar lead por e-mail. leadId={} motivo={}",
                    lead.getId(),
                    ex.getMessage());
        }
    }

    String buildHtml(Lead lead) {
        String mensagem = lead.getMensagem() == null || lead.getMensagem().isBlank()
                ? "—"
                : EmailTextSanitizer.html(lead.getMensagem()).replace("\n", "<br>");

        return """
                <h2>Novo lead no site TRCon</h2>
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

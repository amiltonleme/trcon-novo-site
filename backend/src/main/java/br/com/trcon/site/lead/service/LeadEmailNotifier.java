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
            resendEmailClient.sendHtml(
                    mailProperties.notifyTo(),
                    LeadNotificationMessageBuilder.subject(lead),
                    LeadNotificationMessageBuilder.html(lead),
                    EmailTextSanitizer.header(lead.getEmail()));
            log.info("Notificacao de lead enviada. leadId={} to={}", lead.getId(), mailProperties.notifyTo());
        } catch (RuntimeException ex) {
            log.error(
                    "Falha ao notificar lead por e-mail. leadId={} motivo={}",
                    lead.getId(),
                    ex.getMessage());
        }
    }
}

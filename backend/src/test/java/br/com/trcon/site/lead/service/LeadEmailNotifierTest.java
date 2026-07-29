package br.com.trcon.site.lead.service;

import br.com.trcon.site.lead.domain.Lead;
import br.com.trcon.site.lead.domain.LeadType;
import br.com.trcon.site.lead.integration.ResendEmailClient;
import br.com.trcon.site.shared.config.MailProperties;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class LeadEmailNotifierTest {

    @Mock
    private ResendEmailClient resendEmailClient;

    @Test
    void naoEnviaQuandoMailNaoConfigurado() {
        MailProperties props = new MailProperties(false, "", "", "amilton.leme@trcongroup.com.br");
        LeadEmailNotifier notifier = new LeadEmailNotifier(props, resendEmailClient);
        Lead lead = Lead.novo("Ana", "ana@ex.com", "11", LeadType.PRODUTO, "oi", "site-trcon-hub", true);

        notifier.notifyNewLead(lead);

        verify(resendEmailClient, never()).sendHtml(anyString(), anyString(), anyString(), anyString());
    }

    @Test
    void enviaComConteudoEscapadoEReplyToDoLead() {
        MailProperties props = new MailProperties(
                true, "re_test", "Site TRCon <noreply@trcongroup.com.br>", "amilton.leme@trcongroup.com.br");
        LeadEmailNotifier notifier = new LeadEmailNotifier(props, resendEmailClient);
        Lead lead = Lead.novo(
                "Ana <script>",
                "ana@ex.com",
                "11",
                LeadType.PRODUTO,
                "quero <b>beta</b>\nlinha2",
                "site-trcon-hub",
                true);

        notifier.notifyNewLead(lead);

        ArgumentCaptor<String> subject = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> html = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> replyTo = ArgumentCaptor.forClass(String.class);
        verify(resendEmailClient)
                .sendHtml(eq("amilton.leme@trcongroup.com.br"), subject.capture(), html.capture(), replyTo.capture());

        assertThat(subject.getValue()).contains("PRODUTO").contains("site-trcon-hub");
        assertThat(subject.getValue()).doesNotContain("\n");
        assertThat(html.getValue()).contains("Ana &lt;script&gt;");
        assertThat(html.getValue()).contains("quero &lt;b&gt;beta&lt;/b&gt;<br>linha2");
        assertThat(html.getValue()).doesNotContain("<script>");
        assertThat(replyTo.getValue()).isEqualTo("ana@ex.com");
    }

    @Test
    void enviaMensagemVaziaComoTraco() {
        MailProperties props = new MailProperties(
                true, "re_test", "noreply@trcongroup.com.br", "amilton.leme@trcongroup.com.br");
        LeadEmailNotifier notifier = new LeadEmailNotifier(props, resendEmailClient);
        Lead lead = Lead.novo("Ana", "ana@ex.com", "11", LeadType.PRODUTO, null, "site-trcon", true);

        notifier.notifyNewLead(lead);

        ArgumentCaptor<String> html = ArgumentCaptor.forClass(String.class);
        verify(resendEmailClient).sendHtml(anyString(), anyString(), html.capture(), anyString());
        assertThat(html.getValue()).contains("<br>—");
    }

    @Test
    void absorveFalhaDoResend() {
        MailProperties props = new MailProperties(
                true, "re_test", "noreply@trcongroup.com.br", "amilton.leme@trcongroup.com.br");
        LeadEmailNotifier notifier = new LeadEmailNotifier(props, resendEmailClient);
        Lead lead = Lead.novo("Ana", "ana@ex.com", "11", LeadType.PRODUTO, "oi", "site-trcon", true);

        doThrow(new RuntimeException("Resend down"))
                .when(resendEmailClient)
                .sendHtml(anyString(), anyString(), anyString(), any());

        assertThatCode(() -> notifier.notifyNewLead(lead)).doesNotThrowAnyException();
    }
}

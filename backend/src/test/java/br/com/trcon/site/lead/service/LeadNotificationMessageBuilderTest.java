package br.com.trcon.site.lead.service;

import br.com.trcon.site.lead.domain.Lead;
import br.com.trcon.site.lead.domain.LeadType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LeadNotificationMessageBuilderTest {

    @Test
    void montaSubjectSemQuebraDeLinha() {
        Lead lead = Lead.novo("Ana", "a@b.com", "11", LeadType.PRODUTO, "oi", "site-trcon-hub", true);
        String subject = LeadNotificationMessageBuilder.subject(lead);
        assertThat(subject).contains("PRODUTO").contains("site-trcon-hub").doesNotContain("\n");
    }

    @Test
    void htmlEscapaConteudoEAceitaMensagemVazia() {
        Lead comMsg = Lead.novo("Ana <x>", "a@b.com", "11", LeadType.PRODUTO, "linha1\n<script>", "hub", true);
        String html = LeadNotificationMessageBuilder.html(comMsg);
        assertThat(html).contains("Ana &lt;x&gt;");
        assertThat(html).contains("linha1<br>&lt;script&gt;");
        assertThat(html).doesNotContain("<script>");

        Lead semMsg = Lead.novo("Ana", "a@b.com", "11", LeadType.PRODUTO, "  ", "hub", true);
        assertThat(LeadNotificationMessageBuilder.html(semMsg)).contains("<br>—");

        Lead nullMsg = Lead.novo("Ana", "a@b.com", "11", LeadType.PRODUTO, null, "hub", true);
        assertThat(LeadNotificationMessageBuilder.html(nullMsg)).contains("<br>—");
    }
}

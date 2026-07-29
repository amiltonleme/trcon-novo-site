package br.com.trcon.site.lead.service;

import br.com.trcon.site.lead.domain.Lead;

public interface LeadNotifier {

    void notifyNewLead(Lead lead);
}

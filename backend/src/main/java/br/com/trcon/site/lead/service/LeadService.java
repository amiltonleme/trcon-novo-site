package br.com.trcon.site.lead.service;

import br.com.trcon.site.lead.dto.request.LeadCreateRequest;
import br.com.trcon.site.lead.dto.response.LeadCreateResponse;

public interface LeadService {

    LeadCreateResponse criar(LeadCreateRequest request);
}

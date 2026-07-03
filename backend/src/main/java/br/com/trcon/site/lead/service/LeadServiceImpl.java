package br.com.trcon.site.lead.service;

import br.com.trcon.site.lead.domain.Lead;
import br.com.trcon.site.lead.domain.LeadDuplicadoException;
import br.com.trcon.site.lead.dto.request.LeadCreateRequest;
import br.com.trcon.site.lead.dto.response.LeadCreateResponse;
import br.com.trcon.site.lead.mapper.LeadMapper;
import br.com.trcon.site.lead.repository.LeadRepository;
import br.com.trcon.site.shared.vo.Email;
import org.springframework.stereotype.Service;

@Service
public class LeadServiceImpl implements LeadService {

    private final LeadRepository leadRepository;
    private final LeadMapper leadMapper;

    public LeadServiceImpl(LeadRepository leadRepository, LeadMapper leadMapper) {
        this.leadRepository = leadRepository;
        this.leadMapper = leadMapper;
    }

    @Override
    public LeadCreateResponse criar(LeadCreateRequest request) {
        String emailNormalizado = Email.of(request.email()).value();

        if (leadRepository.existsByEmailAndOrigem(emailNormalizado, request.origem())) {
            throw new LeadDuplicadoException(emailNormalizado, request.origem());
        }

        Lead lead = leadMapper.toDomain(request, emailNormalizado);
        Lead salvo = leadRepository.save(lead);
        return leadMapper.toResponse(salvo);
    }
}

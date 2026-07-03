package br.com.trcon.site.lead.repository;

import br.com.trcon.site.lead.domain.Lead;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface LeadRepository extends JpaRepository<Lead, UUID> {

    boolean existsByEmailAndOrigem(String email, String origem);
}

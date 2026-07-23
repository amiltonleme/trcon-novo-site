package br.com.trcon.site.highlights.repository;

import br.com.trcon.site.highlights.domain.DailyHighlight;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface HighlightRepository extends JpaRepository<DailyHighlight, UUID> {

    List<DailyHighlight> findByActiveTrueOrderByPriorityAscPublishedAtDesc(Limit limit);

    Optional<DailyHighlight> findByExternalId(String externalId);
}

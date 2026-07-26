package br.com.trcon.site.economytips.repository;

import br.com.trcon.site.economytips.domain.EconomyTip;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EconomyTipRepository extends JpaRepository<EconomyTip, UUID> {

    List<EconomyTip> findByActiveTrueOrderByPriorityAscPublishedAtDesc(Limit limit);

    Optional<EconomyTip> findByExternalId(String externalId);
}

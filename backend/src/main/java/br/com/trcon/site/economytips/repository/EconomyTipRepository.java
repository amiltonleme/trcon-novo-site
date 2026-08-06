package br.com.trcon.site.economytips.repository;

import br.com.trcon.site.economytips.domain.EconomyTip;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EconomyTipRepository extends JpaRepository<EconomyTip, UUID> {

    @Query("""
            SELECT e FROM EconomyTip e
            WHERE e.active = true
              AND (e.expiresAt IS NULL OR e.expiresAt > :now)
            ORDER BY e.priority ASC, e.publishedAt DESC
            """)
    List<EconomyTip> findVisible(@Param("now") Instant now, Limit limit);

    Optional<EconomyTip> findByExternalId(String externalId);

    @Modifying(clearAutomatically = true)
    @Query("""
            UPDATE EconomyTip e
            SET e.active = false, e.updatedAt = :now
            WHERE e.active = true
              AND e.expiresAt IS NOT NULL
              AND e.expiresAt <= :now
            """)
    int deactivateExpired(@Param("now") Instant now);
}

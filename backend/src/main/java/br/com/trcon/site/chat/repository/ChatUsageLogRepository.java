package br.com.trcon.site.chat.repository;

import br.com.trcon.site.chat.domain.ChatUsageLog;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ChatUsageLogRepository extends JpaRepository<ChatUsageLog, UUID> {
    @Query("select coalesce(sum(c.estimatedCostUsd), 0) from ChatUsageLog c where c.createdAt >= :start")
    BigDecimal sumCostSince(@Param("start") Instant start);
}

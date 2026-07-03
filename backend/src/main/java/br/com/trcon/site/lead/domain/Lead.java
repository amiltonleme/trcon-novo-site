package br.com.trcon.site.lead.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "leads")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Lead {

    @Id
    private UUID id;

    @Column(nullable = false, length = 160)
    private String nome;

    @Column(nullable = false, length = 160)
    private String email;

    @Column(nullable = false, length = 40)
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_interesse", nullable = false, length = 40)
    private LeadType tipoInteresse;

    @Column(length = 4000)
    private String mensagem;

    @Column(nullable = false, length = 80)
    private String origem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private LeadStatus status;

    @Column(name = "consentimento_lgpd", nullable = false)
    private boolean consentimentoLgpd;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    private Lead(UUID id, String nome, String email, String telefone, LeadType tipoInteresse,
                 String mensagem, String origem, boolean consentimentoLgpd, Instant now) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.telefone = telefone;
        this.tipoInteresse = tipoInteresse;
        this.mensagem = mensagem;
        this.origem = origem;
        this.status = LeadStatus.PENDING;
        this.consentimentoLgpd = consentimentoLgpd;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public static Lead novo(String nome, String email, String telefone, LeadType tipoInteresse,
                             String mensagem, String origem, boolean consentimentoLgpd) {
        Instant now = Instant.now();
        return new Lead(UUID.randomUUID(), nome, email, telefone, tipoInteresse, mensagem, origem,
                consentimentoLgpd, now);
    }

    public void marcarComoContatado() {
        this.status = LeadStatus.CONTACTED;
        this.updatedAt = Instant.now();
    }
}

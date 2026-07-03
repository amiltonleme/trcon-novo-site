package br.com.trcon.site.lead.dto.request;

import br.com.trcon.site.lead.domain.LeadType;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record LeadCreateRequest(

        @NotBlank(message = "nome é obrigatório")
        @Size(max = 160, message = "nome deve ter no máximo 160 caracteres")
        String nome,

        @NotBlank(message = "email é obrigatório")
        @Email(message = "deve ser um email válido")
        @Size(max = 160, message = "email deve ter no máximo 160 caracteres")
        String email,

        @NotBlank(message = "telefone é obrigatório")
        @Size(max = 40, message = "telefone deve ter no máximo 40 caracteres")
        String telefone,

        @NotNull(message = "tipoInteresse é obrigatório")
        LeadType tipoInteresse,

        @Size(max = 4000, message = "mensagem deve ter no máximo 4000 caracteres")
        String mensagem,

        @NotBlank(message = "origem é obrigatória")
        @Size(max = 80, message = "origem deve ter no máximo 80 caracteres")
        String origem,

        @AssertTrue(message = "deve ser verdadeiro")
        boolean consentimentoLgpd
) {
}

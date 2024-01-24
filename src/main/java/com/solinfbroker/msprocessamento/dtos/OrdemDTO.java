package com.solinfbroker.msprocessamento.dtos;

import com.solinfbroker.msprocessamento.model.enumTipoOrdem;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.validation.constraints.Min;

public record OrdemDTO(

        @Column(name = "id_cliente", nullable = false)
        Long idCliente,

        @Column(name = "id_ativo", nullable = false)
        Long idAtivo,

        @Column(name = "tipo_ordem", nullable = false, length = 12)
        @Enumerated(EnumType.STRING)
        enumTipoOrdem tipoOrdem,

        @Column(name = "valor_ordem", nullable = false)
        @Min(1)
        double valorOrdem,
        @Column(name = "valor_cliente_bloqueado", nullable = false)
        double valorClienteBloqueado,

        @Column(name = "quantidade_ordem", nullable = false)
        @Min(1)
        Integer quantidadeOrdem

) {
}

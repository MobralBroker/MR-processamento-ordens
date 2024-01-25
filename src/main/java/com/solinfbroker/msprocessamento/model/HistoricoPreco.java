package com.solinfbroker.msprocessamento.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode
@Table(name = "historico_preco")
public class HistoricoPreco {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_ativo", nullable = false)
    private Long idAtivo;

    @Column(name = "valor_do_ativo", nullable = false)
    private double valor_do_ativo;

    @Column(name = "data_valor", nullable = false)
    private LocalDateTime dataValor;

    @ManyToOne
    @JoinColumn(name = "id_ativo", referencedColumnName = "id", insertable = false, updatable = false)
    private AtivoModel ativoModel;
}

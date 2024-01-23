package com.solinfbroker.msprocessamento.model;

import com.solinfbroker.msprocessamento.dtos.OperacaoDTO;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Data
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "ordem")
public class Ordem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_cliente", nullable = false)
    private Long idCliente;

    @Column(name = "id_ativo", nullable = false)
    private Long idAtivo;

    @Version
    private Long versao;

    @Column(name = "tipo_ordem", nullable = false, length = 12)
    @Enumerated(EnumType.STRING)
    private enumTipoOrdem tipoOrdem;

    @Column(name = "valor_ordem", nullable = false)
    @Min(1)
    private double valorOrdem;

    @Column(name = "data_lancamento", nullable = false)
    private LocalDateTime dataLancamento;

    @Column(name = "quantidade_ordem", nullable = false)
    @Min(1)
    private Integer quantidadeOrdem;

    @Column(name = "quantidade_ordem_aberta", nullable = false)
    private Integer quantidadeAberto;

    @Column(name = "status_ordem", nullable = false, length = 10)
    @Enumerated(EnumType.STRING)
    private enumStatus statusOrdem;

    @ManyToOne
    @JoinColumn(name = "id_cliente", referencedColumnName = "id", insertable = false, updatable = false)
    private ClienteModel cliente;

    @ManyToOne
    @JoinColumn(name = "id_ativo", referencedColumnName = "id", insertable = false, updatable = false)
    private AtivoModel ativo;

    @Transient
    @Nullable
    private List<OperacaoDTO> operacoes;

    @Transient
    @Nullable
    private String sigla;

}

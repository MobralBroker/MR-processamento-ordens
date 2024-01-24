package com.solinfbroker.msprocessamento.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Data
@Getter
@Setter
@EqualsAndHashCode
@Table(name = "operacao")
public class Operacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_compra",insertable=false, updatable=false)
    private Long idCompra;

    @Column(name = "id_venda",insertable=false, updatable=false)
    private Long idVenda;

    @Column(name = "quantidade",nullable = false)
    private Integer quantidade;

    @Column(name = "data_execucao",nullable = false)
    private LocalDateTime dataExecucao;

    @Column(name = "valor_ativo_execucao",nullable = false)
    private double valorAtivoExecucao;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_operacao",nullable = false)
    private enumStatus statusOperacao;

    @ManyToOne
    @JoinColumn(name = "id_compra", referencedColumnName = "id")//,nullable = false, updatable = false)//, referencedColumnName = "id", insertable = false, updatable = false)
    private Ordem ordemCompra;

    @ManyToOne
    @JoinColumn(name = "id_venda", referencedColumnName = "id")//,nullable = false, updatable = false)//, referencedColumnName = "id", insertable = false, updatable = false)
    private Ordem ordemVenda;
}

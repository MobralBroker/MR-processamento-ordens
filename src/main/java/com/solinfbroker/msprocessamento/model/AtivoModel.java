package com.solinfbroker.msprocessamento.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode
@Table(name = "ativo")
public class AtivoModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "id_empresa", nullable = false)
    private Long idEmpresa;

    @Column(name = "sigla", nullable = false, length = 10)
    private String sigla;

    @Column(name = "nome", nullable = false, length = 50)
    private String nome;

    @Column(name = "atualizacao", nullable = false)
    @JsonFormat(pattern="dd/MM/yyyy")
    private LocalDateTime atualizacao;

    @Column(name = "quantidades_papeis", nullable = false)
    private Integer quantidadesPapeis;

    @Column(name = "valor_max", nullable = false)
    private double valorMax;

    @Column(name = "valor_min", nullable = false)
    private double valorMin;

    @Column(name = "valor", nullable = false)
    private double valor;

    @ManyToOne
    @JoinColumn(name = "id_empresa", referencedColumnName = "id", insertable = false, updatable = false)
    private EmpresaModel empresa;

}

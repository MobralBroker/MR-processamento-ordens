package com.solinfbroker.msprocessamento.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Data
@EqualsAndHashCode
@Table(name = "carteira")
public class CarteiraModel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "id_cliente", nullable = false)
  private Long idCliente;

  @Column(name = "id_ativo", nullable = false)
  private Long idAtivo;

  @Column(nullable = false)
  private Integer quantidade;

  @Column(nullable = false)
  private Integer quantidadeBloqueada;

  @Column(name = "data_compra", nullable = false)
  @JsonFormat(pattern="dd/MM/yyyy")
  private LocalDateTime dataCompra;

  @OneToOne
  @JoinColumn(name = "id_cliente", referencedColumnName = "id", insertable = false, updatable = false)
  private ClienteModel cliente;

  @OneToOne
  @JoinColumn(name = "id_ativo", referencedColumnName = "id", insertable = false, updatable = false)
  private AtivoModel ativo;
}
package com.solinfbroker.msprocessamento.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Entity
@Data
@EqualsAndHashCode
@Table(name = "empresa")
public class EmpresaModel {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name = "razao_social", nullable = false, length = 150)
  private String razaoSocial;

  @Column(name = "nome_fantasia", nullable = false, length = 150)
  private String nomeFantasia;

  @Column(name = "cnpj", nullable = false, length = 14)
  private String cnpj;
}
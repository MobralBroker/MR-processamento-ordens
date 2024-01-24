package com.solinfbroker.msprocessamento.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.solinfbroker.msprocessamento.model.enumStatus;
import com.solinfbroker.msprocessamento.model.enumTipoOrdem;
import lombok.Data;
@Data
public class OrdemKafka {

    Integer id;

    @JsonProperty("id_cliente")
    Long idCliente;

    @JsonProperty("id_ativo")
    Long idAtivo;

    @JsonProperty("tipo_ordem")
    enumTipoOrdem tipoOrdem;

    @JsonProperty("valor_ordem")
    double valorOrdem;

    @JsonProperty("valor_cliente_bloqueado")
    double valorClienteBloqueado;

    @JsonProperty("quantidade_ordem")
    Integer quantidadeOrdem;

    @JsonProperty("quantidade_ordem_aberta")
    Integer quantidadeOrdemAberta;

    @JsonProperty("status_ordem")
    enumStatus statusOrdem;

    @JsonProperty("data_lancamento")
    Long dataLancamento;

    @JsonProperty("versao")
    Integer versao;

    @JsonProperty("__op")
    String operacao;



}

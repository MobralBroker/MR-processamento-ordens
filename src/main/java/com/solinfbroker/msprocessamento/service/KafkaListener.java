package com.solinfbroker.msprocessamento.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.solinfbroker.msprocessamento.config.kafka.Topicos;
import com.solinfbroker.msprocessamento.dtos.OrdemKafka;
import com.solinfbroker.msprocessamento.model.enumTipoOrdem;
import lombok.AllArgsConstructor;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;

@Component
@AllArgsConstructor
public class KafkaListener {

    private final ProcessamentoService processamentoService;

    @org.springframework.kafka.annotation.KafkaListener(topics = Topicos.TOPIC_ORDEM,  groupId = Topicos.TOPIC_ORDEM)
    public void listeningOrdem(ConsumerRecord<String, LinkedHashMap<String,Object>> mensagem){
        ObjectMapper objectMapper = new ObjectMapper();
        OrdemKafka ordemKafka = objectMapper.convertValue(mensagem.value(), OrdemKafka.class);
        if(ordemKafka != null){
            if(ordemKafka.getOperacao().equals("c")){
                if(ordemKafka.getTipoOrdem().equals(enumTipoOrdem.ORDEM_COMPRA)){
                    processamentoService.processarOrdemCompra(ordemKafka);

                }else{
                    processamentoService.processarOrdemVenda(ordemKafka);
                }
            }

        }
    }

}


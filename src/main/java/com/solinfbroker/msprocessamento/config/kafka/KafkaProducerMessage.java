package com.solinfbroker.msprocessamento.config.kafka;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class KafkaProducerMessage {

    private final Logger log = LoggerFactory.getLogger(KafkaProducerMessage.class);

    private final KafkaAdminConfig kafkaAdminConfig;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    public void sendMessage(Object message, String topic){
        log.info("PRODUCER sent with Kafka ::: {}", message);
        kafkaTemplate.send(topic, message);
    }

}

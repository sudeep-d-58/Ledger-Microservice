package com.command.ledger.publisher;

import com.command.ledger.model.MessageEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class KafkaMessagePublisher implements MessagePublisher {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;


    @Override
    public void publish(MessageEvent event) {
        kafkaTemplate.send(event.getDestination(), event.getPayload());
        log.info(event.getPayload().toString());
    }
}

package org.egov.noc.producer;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class Producer {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void push(String topic, Object value) {
    	System.out.println("-------------------------------------------------");
    	System.out.println(value);
    	System.out.println("-------------------------------------------------");
        kafkaTemplate.send(topic, value);
    }
}

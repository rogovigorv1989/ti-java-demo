package ru.t1.java.demo.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MetricsProducer<T extends Message> {

    @Autowired
    private KafkaTemplate<String, Message> template;

    public MetricsProducer(@Qualifier("metricsKafkaTemplate") KafkaTemplate<String, Message> template) {
        this.template = template;
    }
}

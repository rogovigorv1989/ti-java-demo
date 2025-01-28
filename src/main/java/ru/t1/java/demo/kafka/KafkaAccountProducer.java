package ru.t1.java.demo.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.AccountDTO;

@Slf4j
@Component
public class KafkaAccountProducer<T extends AccountDTO> {

    @Autowired
    private KafkaTemplate<String, AccountDTO> kafkaTemplate;

    public KafkaAccountProducer(@Qualifier("accountKafkaTemplate") KafkaTemplate<String, AccountDTO> template) {
        this.kafkaTemplate = template;
    }
}

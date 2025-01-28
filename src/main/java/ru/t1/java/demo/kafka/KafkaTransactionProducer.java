package ru.t1.java.demo.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.TransactionDTO;

@Slf4j
@Component
public class KafkaTransactionProducer <T extends TransactionDTO>{
    @Autowired
    private final KafkaTemplate<String, TransactionDTO> kafkaTemplate;

    public KafkaTransactionProducer(@Qualifier("transactionKafkaTemplate") KafkaTemplate<String,
            TransactionDTO> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }
}

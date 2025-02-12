package ru.t1.java.demo.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.TransactionDTO;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
@Component
public class TransactionProducer<T extends TransactionDTO>{

    private final KafkaTemplate<String, TransactionDTO> template;

    @Autowired
    public TransactionProducer(@Qualifier("transactionKafkaTemplate") KafkaTemplate<String,
            TransactionDTO> kafkaTemplate) {
        this.template = kafkaTemplate;
    }

    public void send(TransactionDTO transaction) {
        try {
            template.sendDefault(UUID.randomUUID().toString(), transaction).get();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            template.flush();
        }
    }

    public void sendTo(String topic, Object o) {
        try {
            template.send(topic, (TransactionDTO) o).get();
            template.send(topic,
                            1,
                            LocalDateTime.now().toEpochSecond(ZoneOffset.of("+03:00")),
                            UUID.randomUUID().toString(),
                            (TransactionDTO) o)
                    .get();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            template.flush();
        }
    }
}

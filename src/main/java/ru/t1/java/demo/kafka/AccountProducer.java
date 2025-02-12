package ru.t1.java.demo.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.AccountDTO;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
@Component
public class AccountProducer<T extends AccountDTO> {

    private final KafkaTemplate<String, AccountDTO> template;

    @Autowired
    public AccountProducer(@Qualifier("accountKafkaTemplate") KafkaTemplate<String, AccountDTO> template) {
        this.template = template;
    }

    public void send(AccountDTO account) {
        try {
            template.sendDefault(UUID.randomUUID().toString(), account).get();

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            template.flush();
        }
    }

    public void sendTo(String topic, Object o) {
        try {
            template.send(topic, (AccountDTO) o).get();
            template.send(topic,
                            1,
                            LocalDateTime.now().toEpochSecond(ZoneOffset.of("+03:00")),
                            UUID.randomUUID().toString(),
                            (AccountDTO) o)
                    .get();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            template.flush();
        }
    }
}

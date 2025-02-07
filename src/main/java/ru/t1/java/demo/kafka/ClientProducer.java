package ru.t1.java.demo.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.ClientDTO;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Slf4j
@Component
public class ClientProducer<T extends ClientDTO> {

    @Autowired
    private final KafkaTemplate<String, ClientDTO> template;

    public ClientProducer(@Qualifier("clientKafkaTemplate") KafkaTemplate<String, ClientDTO> template) {
        this.template = template;
    }

    public void send(ClientDTO client) {
        try {
            template.sendDefault(UUID.randomUUID().toString(), client).get();

        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            template.flush();
        }
    }

    public void sendTo(String topic, Object o) {
        try {
            template.send(topic, (ClientDTO) o).get();
            template.send(topic,
                            1,
                            LocalDateTime.now().toEpochSecond(ZoneOffset.of("+03:00")),
                            UUID.randomUUID().toString(),
                            (ClientDTO) o)
                    .get();
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        } finally {
            template.flush();
        }
    }
}

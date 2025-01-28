package ru.t1.java.demo.kafka;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import ru.t1.java.demo.model.dto.DataSourceErrorLogDTO;

@Slf4j
@Component
public class KafkaMetricsProducer <T extends Message<String>>{

    @Autowired
    private KafkaTemplate<String, DataSourceErrorLogDTO> template;

    public KafkaMetricsProducer(@Qualifier("metricsKafkaTemplate") KafkaTemplate<String, DataSourceErrorLogDTO> template) {
        this.template = template;
    }

//    public void send(Long clientId) {
//        try {
//            template.sendDefault(UUID.randomUUID().toString(), clientId).get();
//
//        } catch (Exception ex) {
//            log.error(ex.getMessage(), ex);
//        } finally {
//            template.flush();
//        }
//    }
//
//    public void sendTo(String topic, Object o) {
//        try {
//            template.send(topic, o).get();
//            template.send(topic,
//                            1,
//                            LocalDateTime.now().toEpochSecond(ZoneOffset.of("+03:00")),
//                            UUID.randomUUID().toString(),
//                            o)
//                    .get();
//        } catch (Exception ex) {
//            log.error(ex.getMessage(), ex);
//        } finally {
//            template.flush();
//        }
//    }
}

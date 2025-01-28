package ru.t1.java.demo.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import ru.t1.java.demo.kafka.KafkaClientProducer;
import ru.t1.java.demo.model.dto.ClientDTO;

@Slf4j
@Configuration
public class ClientKafkaConfig extends AbstractKafkaConfig<ClientDTO> {

    @Value("${t1.kafka.topic.client_id_registered}")
    private String topic;

    @Bean
    public ConsumerFactory<String, ClientDTO> consumerClientListenerFactory() {
        return createConsumerFactory(ClientDTO.class);
    }

    @Bean("kafkaClientListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, ClientDTO>
    kafkaClientListenerContainerFactory(ConsumerFactory<String, ClientDTO> consumerFactory) {
        return createListenerContainerFactory(consumerFactory);
    }

    @Bean("clientKafkaTemplate")
    public KafkaTemplate<String, ClientDTO> kafkaClientTemplate(
            ProducerFactory<String, ClientDTO> producerFactory) {
        return createKafkaTemplate(producerFactory);
    }

    @Bean("producerClient")
    @ConditionalOnProperty(value = "t1.kafka.producer.enable", havingValue = "true", matchIfMissing = true)
    public KafkaClientProducer producerClient(KafkaTemplate<String, ClientDTO> template) {
        template.setDefaultTopic(topic);
        return new KafkaClientProducer(template);
    }

    @Bean("producerClientFactory")
    public ProducerFactory<String, ClientDTO> producerClientFactory() {
        return createProducerFactory();
    }
}

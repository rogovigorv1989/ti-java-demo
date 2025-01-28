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
import ru.t1.java.demo.kafka.KafkaTransactionProducer;
import ru.t1.java.demo.model.dto.TransactionDTO;

@Slf4j
@Configuration
public class TransactionKafkaConfig extends AbstractKafkaConfig<TransactionDTO> {

    @Value("${t1.kafka.topic.transaction_registered}")
    private String topic;

    @Bean
    public ConsumerFactory<String, TransactionDTO> consumerTransactionListenerFactory() {
        return createConsumerFactory(TransactionDTO.class);
    }

    @Bean("kafkaTransactionListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, TransactionDTO>
    kafkaTransactionListenerContainerFactory(ConsumerFactory<String, TransactionDTO> consumerFactory) {
        return createListenerContainerFactory(consumerFactory);
    }

    @Bean("transactionKafkaTemplate")
    public KafkaTemplate<String, TransactionDTO> kafkaTransactionTemplate(
            ProducerFactory<String, TransactionDTO> producerFactory) {
        return createKafkaTemplate(producerFactory);
    }

    @Bean("producerTransaction")
    @ConditionalOnProperty(value = "t1.kafka.producer.enable", havingValue = "true", matchIfMissing = true)
    public KafkaTransactionProducer producerTransaction(KafkaTemplate<String, TransactionDTO> template) {
        template.setDefaultTopic(topic);
        return new KafkaTransactionProducer(template);
    }

    @Bean("producerTransactionFactory")
    public ProducerFactory<String, TransactionDTO> producerTransactionFactory() {
        return createProducerFactory();
    }
}
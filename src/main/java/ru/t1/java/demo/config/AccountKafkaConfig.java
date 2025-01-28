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
import ru.t1.java.demo.kafka.KafkaAccountProducer;
import ru.t1.java.demo.model.dto.AccountDTO;

@Slf4j
@Configuration
public class AccountKafkaConfig extends AbstractKafkaConfig<AccountDTO> {

    @Value("${t1.kafka.topic.account_registered}")
    private String topic;

    @Bean
    public ConsumerFactory<String, AccountDTO> consumerAccountListenerFactory() {
        return createConsumerFactory(AccountDTO.class);
    }

    @Bean("kafkaAccountListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, AccountDTO>
    kafkaAccountListenerContainerFactory(ConsumerFactory<String, AccountDTO> consumerFactory) {
        return createListenerContainerFactory(consumerFactory);
    }

    @Bean("accountKafkaTemplate")
    public KafkaTemplate<String, AccountDTO> kafkaAccountTemplate(
            ProducerFactory<String, AccountDTO> producerFactory) {
        return createKafkaTemplate(producerFactory);
    }

    @Bean("producerAccount")
    @ConditionalOnProperty(value = "t1.kafka.producer.enable", havingValue = "true", matchIfMissing = true)
    public KafkaAccountProducer producerAccount(KafkaTemplate<String, AccountDTO> template) {
        template.setDefaultTopic(topic);
        return new KafkaAccountProducer(template);
    }

    @Bean("producerAccountFactory")
    public ProducerFactory<String, AccountDTO> producerAccountFactory() {
        return createProducerFactory();
    }
}

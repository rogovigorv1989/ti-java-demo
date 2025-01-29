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
import org.springframework.messaging.Message;
import ru.t1.java.demo.kafka.MetricsProducer;

@Slf4j
@Configuration
public class MetricsKafkaConfig extends AbstractKafkaConfig<Message> {

    @Value("${t1.kafka.topic.metrics}")
    private String topic;

    @Bean
    public ConsumerFactory<String, Message> consumerMetricsListenerFactory() {
        return createConsumerFactory(Message.class);
    }

    @Bean("kafkmetricsListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, Message>
    kafkaMetricsListenerContainerFactory(ConsumerFactory<String, Message> consumerFactory) {
        return createListenerContainerFactory(consumerFactory);
    }

    @Bean("metricsKafkaTemplate")
    public KafkaTemplate<String, Message> kafkaMetricsTemplate(
            ProducerFactory<String, Message> producerFactory) {
        return createKafkaTemplate(producerFactory);
    }

    @Bean("producerMetrics")
    @ConditionalOnProperty(value = "t1.kafka.producer.enable", havingValue = "true", matchIfMissing = true)
    public MetricsProducer producerMetrics(KafkaTemplate<String, Message> template) {
        template.setDefaultTopic(topic);
        return new MetricsProducer(template);
    }

    @Bean("producerMetricsFactory")
    public ProducerFactory<String, Message> producerMetricsFactory() {
        return createProducerFactory();
    }
}

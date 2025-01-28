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
import ru.t1.java.demo.kafka.KafkaMetricsProducer;
import ru.t1.java.demo.model.dto.DataSourceErrorLogDTO;

@Slf4j
@Configuration
public class MetricsKafkaConfig extends AbstractKafkaConfig<DataSourceErrorLogDTO> {

    @Value("${t1.kafka.topic.metrics}")
    private String topic;

    @Bean
    public ConsumerFactory<String, DataSourceErrorLogDTO> consumerMetricsListenerFactory() {
        return createConsumerFactory(DataSourceErrorLogDTO.class);
    }

    @Bean("kafkaMetricsListenerContainerFactory")
    public ConcurrentKafkaListenerContainerFactory<String, DataSourceErrorLogDTO>
    kafkaMetricsListenerContainerFactory(ConsumerFactory<String, DataSourceErrorLogDTO> consumerFactory) {
        return createListenerContainerFactory(consumerFactory);
    }

    @Bean("metricsKafkaTemplate")
    public KafkaTemplate<String, DataSourceErrorLogDTO> kafkaMetricsTemplate(
            ProducerFactory<String, DataSourceErrorLogDTO> producerFactory) {
        return createKafkaTemplate(producerFactory);
    }

    @Bean("producerMetrics")
    @ConditionalOnProperty(value = "t1.kafka.producer.enable", havingValue = "true", matchIfMissing = true)
    public KafkaMetricsProducer producerMetrics(KafkaTemplate<String, DataSourceErrorLogDTO> template) {
        template.setDefaultTopic(topic);
        return new KafkaMetricsProducer(template);
    }

    @Bean("producerMetricsFactory")
    public ProducerFactory<String, DataSourceErrorLogDTO> producerMetricsFactory() {
        return createProducerFactory();
    }
}
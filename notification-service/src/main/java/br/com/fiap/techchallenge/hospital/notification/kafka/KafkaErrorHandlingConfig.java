package br.com.fiap.techchallenge.hospital.notification.kafka;

import br.com.fiap.techchallenge.hospital.shared.KafkaTopics;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.KafkaOperations;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

@Configuration
public class KafkaErrorHandlingConfig {

    @Bean
    DefaultErrorHandler defaultErrorHandler(
            KafkaOperations<String, String> kafkaTemplate,
            @Value("${app.kafka.topics.consulta-dlt:" + KafkaTopics.CONSULTA_DLT + "}") String dltTopic
    ) {
        var recoverer = new DeadLetterPublishingRecoverer(
                kafkaTemplate,
                (ConsumerRecord<?, ?> record, Exception exception) -> new org.apache.kafka.common.TopicPartition(dltTopic, record.partition())
        );
        return new DefaultErrorHandler(recoverer, new FixedBackOff(500L, 2L));
    }
}


package br.com.fiap.techchallenge.hospital.notification.kafka;

import br.com.fiap.techchallenge.hospital.notification.service.NotificationProcessor;
import br.com.fiap.techchallenge.hospital.shared.ConsultationChangedEvent;
import br.com.fiap.techchallenge.hospital.shared.ConsultationCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationKafkaConsumer {

    private final NotificationProcessor notificationProcessor;
    private final ObjectMapper objectMapper;

    @KafkaListener(topics = "${app.kafka.topics.consulta-criada}", groupId = "${spring.kafka.consumer.group-id}")
    public void onCreated(String payload) throws Exception {
        var event = objectMapper.readValue(payload, ConsultationCreatedEvent.class);
        notificationProcessor.process(event);
        log.info("Evento de consulta criada processado para notificacao {}", event.eventId());
    }

    @KafkaListener(topics = "${app.kafka.topics.consulta-alterada}", groupId = "${spring.kafka.consumer.group-id}")
    public void onChanged(String payload) throws Exception {
        var event = objectMapper.readValue(payload, ConsultationChangedEvent.class);
        notificationProcessor.process(event);
        log.info("Evento de consulta alterada processado para notificacao {}", event.eventId());
    }
}


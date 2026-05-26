package br.com.fiap.techchallenge.hospital.scheduling.service;

import br.com.fiap.techchallenge.hospital.scheduling.domain.AppointmentEntity;
import br.com.fiap.techchallenge.hospital.shared.ConsultationChangedEvent;
import br.com.fiap.techchallenge.hospital.shared.ConsultationCreatedEvent;
import br.com.fiap.techchallenge.hospital.shared.KafkaTopics;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class AppointmentEventPublisher {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.topics.consulta-criada:" + KafkaTopics.CONSULTA_CRIADA + "}")
    private String consultaCriadaTopic;

    @Value("${app.kafka.topics.consulta-alterada:" + KafkaTopics.CONSULTA_ALTERADA + "}")
    private String consultaAlteradaTopic;

    public void publishCreated(AppointmentEntity appointment) {
        var event = new ConsultationCreatedEvent(
                UUID.randomUUID(),
                ConsultationCreatedEvent.TYPE,
                OffsetDateTime.now(),
                appointment.getId(),
                appointment.getPacienteId(),
                appointment.getMedicoId(),
                appointment.getDataHoraInicio(),
                appointment.getStatus()
        );
        kafkaTemplate.send(consultaCriadaTopic, appointment.getId().toString(), event);
    }

    public void publishChanged(AppointmentEntity appointment) {
        var event = new ConsultationChangedEvent(
                UUID.randomUUID(),
                ConsultationChangedEvent.TYPE,
                OffsetDateTime.now(),
                appointment.getId(),
                appointment.getPacienteId(),
                appointment.getMedicoId(),
                appointment.getDataHoraInicio(),
                appointment.getStatus()
        );
        kafkaTemplate.send(consultaAlteradaTopic, appointment.getId().toString(), event);
    }
}


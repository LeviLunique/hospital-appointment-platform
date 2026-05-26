package br.com.fiap.techchallenge.hospital.notification.service;

import br.com.fiap.techchallenge.hospital.notification.domain.NotificationEntity;
import br.com.fiap.techchallenge.hospital.notification.domain.NotificationStatus;
import br.com.fiap.techchallenge.hospital.notification.domain.ProcessedEventEntity;
import br.com.fiap.techchallenge.hospital.notification.repository.NotificationRepository;
import br.com.fiap.techchallenge.hospital.notification.repository.ProcessedEventRepository;
import br.com.fiap.techchallenge.hospital.shared.AppointmentStatus;
import br.com.fiap.techchallenge.hospital.shared.ConsultationChangedEvent;
import br.com.fiap.techchallenge.hospital.shared.ConsultationCreatedEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationProcessor {

    private static final String TYPE = "LEMBRETE_CONSULTA";

    private final NotificationRepository notificationRepository;
    private final ProcessedEventRepository processedEventRepository;
    private final NotificationSender notificationSender;
    private final ObjectMapper objectMapper;

    @Transactional
    public void process(ConsultationCreatedEvent event) {
        processEvent(event.eventId(), event.consultaId(), event.pacienteId(), event.dataHoraInicio(), event.status(), event);
    }

    @Transactional
    public void process(ConsultationChangedEvent event) {
        processEvent(event.eventId(), event.consultaId(), event.pacienteId(), event.dataHoraInicio(), event.status(), event);
    }

    private void processEvent(
            UUID eventId,
            UUID consultaId,
            UUID pacienteId,
            OffsetDateTime dataHoraInicio,
            AppointmentStatus status,
            Object event
    ) {
        if (processedEventRepository.existsById(eventId)) {
            return;
        }

        if (status == AppointmentStatus.CANCELADA || !dataHoraInicio.isAfter(OffsetDateTime.now())) {
            processedEventRepository.save(ProcessedEventEntity.builder().eventId(eventId).build());
            return;
        }

        var notification = NotificationEntity.builder()
                .id(UUID.randomUUID())
                .consultaId(consultaId)
                .pacienteId(pacienteId)
                .tipo(TYPE)
                .status(NotificationStatus.PENDENTE)
                .payload(toJson(event))
                .tentativas(1)
                .build();

        try {
            notificationSender.sendAppointmentReminder(pacienteId, consultaId, dataHoraInicio);
            notification.setStatus(NotificationStatus.ENVIADA);
            notification.setSentAt(OffsetDateTime.now());
            notificationRepository.save(notification);
            processedEventRepository.save(ProcessedEventEntity.builder().eventId(eventId).build());
        } catch (RuntimeException exception) {
            notification.setStatus(NotificationStatus.FALHA);
            notificationRepository.save(notification);
            throw exception;
        }
    }

    private String toJson(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JsonProcessingException exception) {
            throw new IllegalStateException("Nao foi possivel serializar evento de notificacao", exception);
        }
    }
}


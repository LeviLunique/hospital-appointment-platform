package br.com.fiap.techchallenge.hospital.notification.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Slf4j
@Component
public class LoggingNotificationSender implements NotificationSender {

    @Override
    public void sendAppointmentReminder(UUID pacienteId, UUID consultaId, OffsetDateTime dataHoraInicio) {
        log.info("Lembrete enviado ao paciente {} para consulta {} em {}", pacienteId, consultaId, dataHoraInicio);
    }
}


package br.com.fiap.techchallenge.hospital.notification.service;

import java.time.OffsetDateTime;
import java.util.UUID;

public interface NotificationSender {
    void sendAppointmentReminder(UUID pacienteId, UUID consultaId, OffsetDateTime dataHoraInicio);
}


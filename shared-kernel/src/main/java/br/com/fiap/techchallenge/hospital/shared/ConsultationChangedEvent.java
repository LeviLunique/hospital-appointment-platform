package br.com.fiap.techchallenge.hospital.shared;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ConsultationChangedEvent(
        UUID eventId,
        String eventType,
        OffsetDateTime occurredAt,
        UUID consultaId,
        UUID pacienteId,
        UUID medicoId,
        OffsetDateTime dataHoraInicio,
        AppointmentStatus status
) {
    public static final String TYPE = "consulta.alterada.v1";
}


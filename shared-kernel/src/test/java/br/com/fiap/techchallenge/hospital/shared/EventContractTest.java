package br.com.fiap.techchallenge.hospital.shared;

import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class EventContractTest {

    @Test
    void deveCriarEventoDeConsultaComContratoEstavel() {
        var consultaId = UUID.randomUUID();

        var event = new ConsultationCreatedEvent(
                UUID.randomUUID(),
                ConsultationCreatedEvent.TYPE,
                OffsetDateTime.parse("2026-05-25T10:15:30-03:00"),
                consultaId,
                UUID.randomUUID(),
                UUID.randomUUID(),
                OffsetDateTime.parse("2026-06-10T14:00:00-03:00"),
                AppointmentStatus.AGENDADA
        );

        assertThat(event.eventType()).isEqualTo("consulta.criada.v1");
        assertThat(event.consultaId()).isEqualTo(consultaId);
        assertThat(KafkaTopics.CONSULTA_CRIADA).isEqualTo("hospital.consultas.criadas.v1");
    }
}


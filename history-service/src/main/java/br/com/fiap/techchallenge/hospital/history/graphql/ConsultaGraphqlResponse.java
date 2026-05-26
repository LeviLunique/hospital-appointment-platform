package br.com.fiap.techchallenge.hospital.history.graphql;

import br.com.fiap.techchallenge.hospital.shared.AppointmentStatus;
import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

@Value
@Builder
public class ConsultaGraphqlResponse {
    UUID id;
    UUID pacienteId;
    UUID medicoId;
    OffsetDateTime dataHoraInicio;
    OffsetDateTime dataHoraFim;
    AppointmentStatus status;
    String motivo;
    String observacoesClinicas;
}


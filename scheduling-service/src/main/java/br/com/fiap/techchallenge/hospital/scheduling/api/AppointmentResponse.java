package br.com.fiap.techchallenge.hospital.scheduling.api;

import br.com.fiap.techchallenge.hospital.shared.AppointmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Value;

import java.time.OffsetDateTime;
import java.util.UUID;

@Value
@Builder
@Schema(description = "Representacao de consulta")
public class AppointmentResponse {
    @Schema(example = "007b9aae-e530-4031-b61d-eabd68bc9c01")
    UUID id;
    @Schema(example = "7a3f2f0a-2c1f-4f0a-9368-2d9dbd6e8e44")
    UUID pacienteId;
    @Schema(example = "4d2f88ef-8b8a-4d9a-a96f-808b1774bc2f")
    UUID medicoId;
    @Schema(example = "2026-06-10T14:00:00-03:00")
    OffsetDateTime dataHoraInicio;
    @Schema(example = "2026-06-10T14:30:00-03:00")
    OffsetDateTime dataHoraFim;
    @Schema(example = "AGENDADA")
    AppointmentStatus status;
    @Schema(example = "Retorno clinico")
    String motivo;
    @Schema(example = "Paciente apresentou melhora.")
    String observacoesClinicas;
    OffsetDateTime createdAt;
    OffsetDateTime updatedAt;
}

package br.com.fiap.techchallenge.hospital.scheduling.api;

import br.com.fiap.techchallenge.hospital.shared.AppointmentStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Schema(description = "Payload para alteracao parcial de consulta")
public class UpdateAppointmentRequest {

    @Schema(description = "Novo identificador do paciente", example = "7a3f2f0a-2c1f-4f0a-9368-2d9dbd6e8e44")
    private UUID pacienteId;
    @Schema(description = "Novo identificador do medico", example = "4d2f88ef-8b8a-4d9a-a96f-808b1774bc2f")
    private UUID medicoId;
    @Schema(description = "Novo inicio da consulta em ISO-8601", example = "2026-06-11T09:00:00-03:00")
    private OffsetDateTime dataHoraInicio;
    @Schema(description = "Novo fim da consulta em ISO-8601", example = "2026-06-11T09:30:00-03:00")
    private OffsetDateTime dataHoraFim;
    @Schema(description = "Novo status da consulta", example = "REMARCADA")
    private AppointmentStatus status;
    @Schema(description = "Novo motivo da consulta", example = "Remarcacao solicitada pelo paciente")
    private String motivo;
}

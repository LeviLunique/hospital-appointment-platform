package br.com.fiap.techchallenge.hospital.scheduling.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.OffsetDateTime;
import java.util.UUID;

@Data
@Schema(description = "Payload para criacao de consulta")
public class CreateAppointmentRequest {

    @NotNull
    @Schema(description = "Identificador do paciente", example = "7a3f2f0a-2c1f-4f0a-9368-2d9dbd6e8e44")
    private UUID pacienteId;

    @NotNull
    @Schema(description = "Identificador do medico", example = "4d2f88ef-8b8a-4d9a-a96f-808b1774bc2f")
    private UUID medicoId;

    @NotNull
    @Schema(description = "Inicio da consulta em ISO-8601", example = "2026-06-10T14:00:00-03:00")
    private OffsetDateTime dataHoraInicio;

    @Schema(description = "Fim da consulta em ISO-8601", example = "2026-06-10T14:30:00-03:00")
    private OffsetDateTime dataHoraFim;

    @Schema(description = "Motivo da consulta", example = "Retorno clinico")
    private String motivo;
}

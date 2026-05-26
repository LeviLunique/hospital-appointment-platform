package br.com.fiap.techchallenge.hospital.scheduling.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@Schema(description = "Payload para atualizacao de historico clinico")
public class ClinicalHistoryRequest {

    @NotBlank
    @Schema(description = "Observacoes clinicas registradas pelo medico", example = "Paciente apresentou melhora e deve retornar em 30 dias.")
    private String observacoesClinicas;
}

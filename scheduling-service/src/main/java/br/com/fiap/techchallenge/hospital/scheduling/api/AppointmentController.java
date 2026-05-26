package br.com.fiap.techchallenge.hospital.scheduling.api;

import br.com.fiap.techchallenge.hospital.scheduling.service.AppointmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/consultas")
@RequiredArgsConstructor
@Tag(name = "Consultas", description = "Operacoes REST de agendamento hospitalar")
@SecurityRequirement(name = "bearerAuth")
public class AppointmentController {

    private final AppointmentService appointmentService;

    @PostMapping
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    @Operation(summary = "Criar consulta", description = "Cria uma consulta com status inicial AGENDADA. Permitido para MEDICO e ENFERMEIRO.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Consulta criada",
                    content = @Content(schema = @Schema(implementation = AppointmentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload invalido",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais ausentes ou invalidas"),
            @ApiResponse(responseCode = "403", description = "Perfil sem permissao para criar consulta")
    })
    public ResponseEntity<AppointmentResponse> create(@Valid @RequestBody CreateAppointmentRequest request) {
        var response = appointmentService.create(request);
        return ResponseEntity
                .created(URI.create("/api/v1/consultas/" + response.getId()))
                .body(response);
    }

    @PatchMapping("/{consultaId}")
    @PreAuthorize("hasAnyRole('MEDICO', 'ENFERMEIRO')")
    @Operation(summary = "Alterar consulta", description = "Atualiza dados cadastrais ou status de uma consulta. Permitido para MEDICO e ENFERMEIRO.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Consulta alterada",
                    content = @Content(schema = @Schema(implementation = AppointmentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload invalido",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais ausentes ou invalidas"),
            @ApiResponse(responseCode = "403", description = "Perfil sem permissao para alterar consulta"),
            @ApiResponse(responseCode = "404", description = "Consulta nao encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<AppointmentResponse> update(
            @Parameter(description = "Identificador da consulta") @PathVariable UUID consultaId,
            @Valid @RequestBody UpdateAppointmentRequest request
    ) {
        return ResponseEntity.ok(appointmentService.update(consultaId, request));
    }

    @PatchMapping("/{consultaId}/historico")
    @PreAuthorize("hasRole('MEDICO')")
    @Operation(summary = "Atualizar historico clinico", description = "Registra observacoes clinicas da consulta. Permitido apenas para MEDICO.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Historico clinico atualizado",
                    content = @Content(schema = @Schema(implementation = AppointmentResponse.class))),
            @ApiResponse(responseCode = "400", description = "Payload invalido",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class))),
            @ApiResponse(responseCode = "401", description = "Credenciais ausentes ou invalidas"),
            @ApiResponse(responseCode = "403", description = "Perfil sem permissao para editar historico clinico"),
            @ApiResponse(responseCode = "404", description = "Consulta nao encontrada",
                    content = @Content(schema = @Schema(implementation = ProblemDetail.class)))
    })
    public ResponseEntity<AppointmentResponse> updateClinicalHistory(
            @Parameter(description = "Identificador da consulta") @PathVariable UUID consultaId,
            @Valid @RequestBody ClinicalHistoryRequest request
    ) {
        return ResponseEntity.ok(appointmentService.updateClinicalHistory(consultaId, request));
    }
}

package br.com.fiap.techchallenge.hospital.history.service;

import br.com.fiap.techchallenge.hospital.history.domain.AppointmentHistoryEntity;
import br.com.fiap.techchallenge.hospital.history.graphql.ConsultaGraphqlResponse;
import br.com.fiap.techchallenge.hospital.history.graphql.HistoricoFiltro;
import br.com.fiap.techchallenge.hospital.history.repository.AppointmentHistoryRepository;
import br.com.fiap.techchallenge.hospital.shared.UserRole;
import br.com.fiap.techchallenge.hospital.shared.security.JwtAuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class HistoryQueryService {

    private final AppointmentHistoryRepository historyRepository;

    public List<ConsultaGraphqlResponse> historicoPaciente(UUID pacienteId, HistoricoFiltro filtro, JwtAuthenticatedUser currentUser) {
        if (currentUser.role() == UserRole.PACIENTE && !pacienteId.equals(currentUser.pacienteId())) {
            throw new AccessDeniedException("Paciente nao pode consultar historico de outro paciente");
        }
        return findByFilter(pacienteId, filtro).stream()
                .map(this::toResponse)
                .toList();
    }

    public List<ConsultaGraphqlResponse> minhasConsultas(HistoricoFiltro filtro, JwtAuthenticatedUser currentUser) {
        if (currentUser.role() != UserRole.PACIENTE || currentUser.pacienteId() == null) {
            return List.of();
        }
        return findByFilter(currentUser.pacienteId(), filtro).stream()
                .map(this::toResponse)
                .toList();
    }

    private List<AppointmentHistoryEntity> findByFilter(UUID pacienteId, HistoricoFiltro filtro) {
        if (filtro == HistoricoFiltro.FUTURAS) {
            return historyRepository.findByPacienteIdAndDataHoraInicioAfterOrderByDataHoraInicioAsc(pacienteId, OffsetDateTime.now());
        }
        return historyRepository.findByPacienteIdOrderByDataHoraInicioAsc(pacienteId);
    }

    private ConsultaGraphqlResponse toResponse(AppointmentHistoryEntity entity) {
        return ConsultaGraphqlResponse.builder()
                .id(entity.getId())
                .pacienteId(entity.getPacienteId())
                .medicoId(entity.getMedicoId())
                .dataHoraInicio(entity.getDataHoraInicio())
                .dataHoraFim(entity.getDataHoraFim())
                .status(entity.getStatus())
                .motivo(entity.getMotivo())
                .observacoesClinicas(entity.getObservacoesClinicas())
                .build();
    }
}

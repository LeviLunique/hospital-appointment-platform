package br.com.fiap.techchallenge.hospital.history.service;

import br.com.fiap.techchallenge.hospital.history.graphql.HistoricoFiltro;
import br.com.fiap.techchallenge.hospital.history.repository.AppointmentHistoryRepository;
import br.com.fiap.techchallenge.hospital.shared.UserRole;
import br.com.fiap.techchallenge.hospital.shared.security.JwtAuthenticatedUser;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;

class HistoryQueryServiceTest {

    private final AppointmentHistoryRepository historyRepository = mock(AppointmentHistoryRepository.class);
    private final HistoryQueryService service = new HistoryQueryService(historyRepository);

    @Test
    void naoDevePermitirPacienteConsultarOutroPaciente() {
        var pacienteLogado = UUID.randomUUID();
        var outroPaciente = UUID.randomUUID();
        var principal = new JwtAuthenticatedUser("paciente", UserRole.PACIENTE, pacienteLogado);

        assertThatThrownBy(() -> service.historicoPaciente(outroPaciente, HistoricoFiltro.TODAS, principal))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void deveRetornarVazioParaMedicoConsultandoMinhasConsultas() {
        var principal = new JwtAuthenticatedUser("medico", UserRole.MEDICO, null);

        assertThat(service.minhasConsultas(HistoricoFiltro.TODAS, principal)).isEmpty();
    }
}

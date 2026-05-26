package br.com.fiap.techchallenge.hospital.history.graphql;

import br.com.fiap.techchallenge.hospital.history.service.HistoryQueryService;
import br.com.fiap.techchallenge.hospital.shared.security.JwtAuthenticatedUser;
import lombok.RequiredArgsConstructor;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
@RequiredArgsConstructor
public class HistoryGraphqlController {

    private final HistoryQueryService historyQueryService;

    @QueryMapping
    public List<ConsultaGraphqlResponse> historicoPaciente(
            @Argument UUID pacienteId,
            @Argument HistoricoFiltro filtro,
            Authentication authentication
    ) {
        return historyQueryService.historicoPaciente(pacienteId, normalize(filtro), principal(authentication));
    }

    @QueryMapping
    public List<ConsultaGraphqlResponse> minhasConsultas(
            @Argument HistoricoFiltro filtro,
            Authentication authentication
    ) {
        return historyQueryService.minhasConsultas(normalize(filtro), principal(authentication));
    }

    private HistoricoFiltro normalize(HistoricoFiltro filtro) {
        return filtro == null ? HistoricoFiltro.TODAS : filtro;
    }

    private JwtAuthenticatedUser principal(Authentication authentication) {
        if (authentication == null || !(authentication.getPrincipal() instanceof JwtAuthenticatedUser user)) {
            throw new AccessDeniedException("Token JWT ausente ou invalido");
        }
        return user;
    }
}

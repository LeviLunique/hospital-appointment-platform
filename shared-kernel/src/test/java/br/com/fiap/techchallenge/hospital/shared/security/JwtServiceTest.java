package br.com.fiap.techchallenge.hospital.shared.security;

import br.com.fiap.techchallenge.hospital.shared.UserRole;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class JwtServiceTest {

    private static final String SECRET = "ZmFrZS1qd3Qtc2VjcmV0LXNob3VsZC1iZS1sb25nLWVuY29kZWQ=";

    @Test
    void deveGerarEParsearTokenComClaimsPacienteId() {
        var service = new JwtService(new JwtProperties(SECRET, 60_000));
        UUID pacienteId = UUID.randomUUID();
        var user = new JwtAuthenticatedUser("paciente", UserRole.PACIENTE, pacienteId);

        String token = service.generateToken(user);
        JwtAuthenticatedUser parsed = service.parse(token);

        assertThat(parsed.username()).isEqualTo("paciente");
        assertThat(parsed.role()).isEqualTo(UserRole.PACIENTE);
        assertThat(parsed.pacienteId()).isEqualTo(pacienteId);
    }

    @Test
    void deveGerarTokenSemPacienteIdParaMedico() {
        var service = new JwtService(new JwtProperties(SECRET, 60_000));
        var user = new JwtAuthenticatedUser("medico", UserRole.MEDICO, null);

        String token = service.generateToken(user);
        JwtAuthenticatedUser parsed = service.parse(token);

        assertThat(parsed.role()).isEqualTo(UserRole.MEDICO);
        assertThat(parsed.pacienteId()).isNull();
    }
}

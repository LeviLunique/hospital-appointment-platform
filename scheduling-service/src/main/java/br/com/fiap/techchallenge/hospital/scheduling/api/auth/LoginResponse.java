package br.com.fiap.techchallenge.hospital.scheduling.api.auth;

import br.com.fiap.techchallenge.hospital.shared.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private String tipo;
    private long expiraEmMs;
    private String username;
    private UserRole role;
    private UUID pacienteId;
}

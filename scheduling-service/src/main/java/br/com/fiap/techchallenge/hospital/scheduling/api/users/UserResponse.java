package br.com.fiap.techchallenge.hospital.scheduling.api.users;

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
public class UserResponse {

    private UUID id;
    private String username;
    private UserRole role;
    private UUID pacienteId;
}

package br.com.fiap.techchallenge.hospital.scheduling.api.users;

import br.com.fiap.techchallenge.hospital.shared.UserRole;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserRequest {

    @NotBlank(message = "username e obrigatorio")
    @Size(min = 3, max = 120, message = "username deve ter entre 3 e 120 caracteres")
    private String username;

    @NotBlank(message = "senha e obrigatoria")
    @Size(min = 8, max = 72, message = "senha deve ter entre 8 e 72 caracteres")
    private String senha;

    @NotNull(message = "role e obrigatoria")
    private UserRole role;

    private UUID pacienteId;
}

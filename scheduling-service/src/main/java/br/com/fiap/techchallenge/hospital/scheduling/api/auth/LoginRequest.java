package br.com.fiap.techchallenge.hospital.scheduling.api.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = "username e obrigatorio")
    private String username;

    @NotBlank(message = "senha e obrigatoria")
    private String senha;
}

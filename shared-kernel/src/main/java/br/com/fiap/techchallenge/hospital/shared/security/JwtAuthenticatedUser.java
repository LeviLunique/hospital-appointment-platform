package br.com.fiap.techchallenge.hospital.shared.security;

import br.com.fiap.techchallenge.hospital.shared.UserRole;

import java.util.UUID;

public record JwtAuthenticatedUser(String username, UserRole role, UUID pacienteId) {
}

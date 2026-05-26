package br.com.fiap.techchallenge.hospital.shared.security;

public record JwtProperties(String secret, long expirationMs) {
}

package br.com.fiap.techchallenge.hospital.scheduling.service;

import br.com.fiap.techchallenge.hospital.scheduling.api.auth.LoginRequest;
import br.com.fiap.techchallenge.hospital.scheduling.api.auth.LoginResponse;
import br.com.fiap.techchallenge.hospital.scheduling.repository.UserAccountRepository;
import br.com.fiap.techchallenge.hospital.shared.security.JwtAuthenticatedUser;
import br.com.fiap.techchallenge.hospital.shared.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserAccountRepository userAccountRepository;
    private final JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getSenha())
        );

        var account = userAccountRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario nao encontrado: " + request.getUsername()));

        var principal = new JwtAuthenticatedUser(account.getUsername(), account.getRole(), account.getPacienteId());
        String token = jwtService.generateToken(principal);

        return LoginResponse.builder()
                .token(token)
                .tipo("Bearer")
                .expiraEmMs(jwtService.getExpirationMs())
                .username(account.getUsername())
                .role(account.getRole())
                .pacienteId(account.getPacienteId())
                .build();
    }
}

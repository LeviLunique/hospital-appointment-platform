package br.com.fiap.techchallenge.hospital.scheduling.service;

import br.com.fiap.techchallenge.hospital.scheduling.api.users.CreateUserRequest;
import br.com.fiap.techchallenge.hospital.scheduling.api.users.UserResponse;
import br.com.fiap.techchallenge.hospital.scheduling.domain.UserAccountEntity;
import br.com.fiap.techchallenge.hospital.scheduling.repository.UserAccountRepository;
import br.com.fiap.techchallenge.hospital.shared.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserAccountService {

    private final UserAccountRepository userAccountRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse create(CreateUserRequest request) {
        userAccountRepository.findByUsername(request.getUsername()).ifPresent(existing -> {
            throw new UsernameAlreadyExistsException(request.getUsername());
        });

        UUID pacienteId = request.getRole() == UserRole.PACIENTE
                ? (request.getPacienteId() != null ? request.getPacienteId() : UUID.randomUUID())
                : null;

        UserAccountEntity entity = UserAccountEntity.builder()
                .id(UUID.randomUUID())
                .username(request.getUsername())
                .passwordHash(passwordEncoder.encode(request.getSenha()))
                .role(request.getRole())
                .pacienteId(pacienteId)
                .build();

        UserAccountEntity saved = userAccountRepository.save(entity);

        return UserResponse.builder()
                .id(saved.getId())
                .username(saved.getUsername())
                .role(saved.getRole())
                .pacienteId(saved.getPacienteId())
                .build();
    }
}

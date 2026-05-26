package br.com.fiap.techchallenge.hospital.scheduling.repository;

import br.com.fiap.techchallenge.hospital.scheduling.domain.UserAccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserAccountRepository extends JpaRepository<UserAccountEntity, UUID> {
    Optional<UserAccountEntity> findByUsername(String username);
}


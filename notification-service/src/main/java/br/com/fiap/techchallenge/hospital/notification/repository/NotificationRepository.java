package br.com.fiap.techchallenge.hospital.notification.repository;

import br.com.fiap.techchallenge.hospital.notification.domain.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NotificationRepository extends JpaRepository<NotificationEntity, UUID> {
}


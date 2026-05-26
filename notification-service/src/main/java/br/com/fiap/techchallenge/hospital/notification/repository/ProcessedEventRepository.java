package br.com.fiap.techchallenge.hospital.notification.repository;

import br.com.fiap.techchallenge.hospital.notification.domain.ProcessedEventEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ProcessedEventRepository extends JpaRepository<ProcessedEventEntity, UUID> {
}


package br.com.fiap.techchallenge.hospital.scheduling.repository;

import br.com.fiap.techchallenge.hospital.scheduling.domain.AppointmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AppointmentRepository extends JpaRepository<AppointmentEntity, UUID> {
}


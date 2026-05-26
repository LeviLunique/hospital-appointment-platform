package br.com.fiap.techchallenge.hospital.history.repository;

import br.com.fiap.techchallenge.hospital.history.domain.AppointmentHistoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public interface AppointmentHistoryRepository extends JpaRepository<AppointmentHistoryEntity, UUID> {
    List<AppointmentHistoryEntity> findByPacienteIdOrderByDataHoraInicioAsc(UUID pacienteId);
    List<AppointmentHistoryEntity> findByPacienteIdAndDataHoraInicioAfterOrderByDataHoraInicioAsc(UUID pacienteId, OffsetDateTime now);
}


package br.com.fiap.techchallenge.hospital.scheduling.service;

import br.com.fiap.techchallenge.hospital.scheduling.api.AppointmentResponse;
import br.com.fiap.techchallenge.hospital.scheduling.api.ClinicalHistoryRequest;
import br.com.fiap.techchallenge.hospital.scheduling.api.CreateAppointmentRequest;
import br.com.fiap.techchallenge.hospital.scheduling.api.UpdateAppointmentRequest;
import br.com.fiap.techchallenge.hospital.scheduling.domain.AppointmentEntity;
import br.com.fiap.techchallenge.hospital.scheduling.repository.AppointmentRepository;
import br.com.fiap.techchallenge.hospital.shared.AppointmentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AppointmentService {

    private final AppointmentRepository appointmentRepository;
    private final AppointmentEventPublisher eventPublisher;
    private final AppointmentMapper mapper;

    @Transactional
    public AppointmentResponse create(CreateAppointmentRequest request) {
        var appointment = AppointmentEntity.builder()
                .id(UUID.randomUUID())
                .pacienteId(request.getPacienteId())
                .medicoId(request.getMedicoId())
                .dataHoraInicio(request.getDataHoraInicio())
                .dataHoraFim(request.getDataHoraFim())
                .status(AppointmentStatus.AGENDADA)
                .motivo(request.getMotivo())
                .build();

        var saved = appointmentRepository.save(appointment);
        eventPublisher.publishCreated(saved);
        return mapper.toResponse(saved);
    }

    @Transactional
    public AppointmentResponse update(UUID consultaId, UpdateAppointmentRequest request) {
        var appointment = appointmentRepository.findById(consultaId)
                .orElseThrow(() -> new AppointmentNotFoundException(consultaId));

        if (request.getPacienteId() != null) {
            appointment.setPacienteId(request.getPacienteId());
        }
        if (request.getMedicoId() != null) {
            appointment.setMedicoId(request.getMedicoId());
        }
        if (request.getDataHoraInicio() != null) {
            appointment.setDataHoraInicio(request.getDataHoraInicio());
        }
        if (request.getDataHoraFim() != null) {
            appointment.setDataHoraFim(request.getDataHoraFim());
        }
        if (request.getStatus() != null) {
            appointment.setStatus(request.getStatus());
        }
        if (request.getMotivo() != null) {
            appointment.setMotivo(request.getMotivo());
        }

        var saved = appointmentRepository.save(appointment);
        eventPublisher.publishChanged(saved);
        return mapper.toResponse(saved);
    }

    @Transactional
    public AppointmentResponse updateClinicalHistory(UUID consultaId, ClinicalHistoryRequest request) {
        var appointment = appointmentRepository.findById(consultaId)
                .orElseThrow(() -> new AppointmentNotFoundException(consultaId));
        appointment.setObservacoesClinicas(request.getObservacoesClinicas());
        return mapper.toResponse(appointmentRepository.save(appointment));
    }
}


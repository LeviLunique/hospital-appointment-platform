package br.com.fiap.techchallenge.hospital.scheduling.service;

import br.com.fiap.techchallenge.hospital.scheduling.api.AppointmentResponse;
import br.com.fiap.techchallenge.hospital.scheduling.domain.AppointmentEntity;
import org.springframework.stereotype.Component;

@Component
public class AppointmentMapper {

    public AppointmentResponse toResponse(AppointmentEntity entity) {
        return AppointmentResponse.builder()
                .id(entity.getId())
                .pacienteId(entity.getPacienteId())
                .medicoId(entity.getMedicoId())
                .dataHoraInicio(entity.getDataHoraInicio())
                .dataHoraFim(entity.getDataHoraFim())
                .status(entity.getStatus())
                .motivo(entity.getMotivo())
                .observacoesClinicas(entity.getObservacoesClinicas())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}


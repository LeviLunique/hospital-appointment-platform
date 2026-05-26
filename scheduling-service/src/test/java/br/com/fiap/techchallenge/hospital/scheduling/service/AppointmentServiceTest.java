package br.com.fiap.techchallenge.hospital.scheduling.service;

import br.com.fiap.techchallenge.hospital.scheduling.api.CreateAppointmentRequest;
import br.com.fiap.techchallenge.hospital.scheduling.api.UpdateAppointmentRequest;
import br.com.fiap.techchallenge.hospital.scheduling.domain.AppointmentEntity;
import br.com.fiap.techchallenge.hospital.scheduling.repository.AppointmentRepository;
import br.com.fiap.techchallenge.hospital.shared.AppointmentStatus;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class AppointmentServiceTest {

    private final AppointmentRepository repository = mock(AppointmentRepository.class);
    private final AppointmentEventPublisher publisher = mock(AppointmentEventPublisher.class);
    private final AppointmentService service = new AppointmentService(repository, publisher, new AppointmentMapper());

    @Test
    void deveCriarConsultaEPublicarEvento() {
        var request = new CreateAppointmentRequest();
        request.setPacienteId(UUID.randomUUID());
        request.setMedicoId(UUID.randomUUID());
        request.setDataHoraInicio(OffsetDateTime.parse("2026-06-10T14:00:00-03:00"));
        request.setMotivo("Retorno clinico");

        when(repository.save(any(AppointmentEntity.class))).thenAnswer(invocation -> {
            var entity = invocation.getArgument(0, AppointmentEntity.class);
            entity.prePersist();
            return entity;
        });

        var response = service.create(request);

        assertThat(response.getStatus()).isEqualTo(AppointmentStatus.AGENDADA);
        assertThat(response.getPacienteId()).isEqualTo(request.getPacienteId());
        verify(publisher).publishCreated(any(AppointmentEntity.class));
    }

    @Test
    void deveAlterarConsultaEPublicarEvento() {
        var consultaId = UUID.randomUUID();
        var appointment = AppointmentEntity.builder()
                .id(consultaId)
                .pacienteId(UUID.randomUUID())
                .medicoId(UUID.randomUUID())
                .dataHoraInicio(OffsetDateTime.parse("2026-06-10T14:00:00-03:00"))
                .status(AppointmentStatus.AGENDADA)
                .build();
        appointment.prePersist();

        var request = new UpdateAppointmentRequest();
        request.setStatus(AppointmentStatus.REMARCADA);
        request.setDataHoraInicio(OffsetDateTime.parse("2026-06-11T09:00:00-03:00"));

        when(repository.findById(consultaId)).thenReturn(Optional.of(appointment));
        when(repository.save(any(AppointmentEntity.class))).thenAnswer(invocation -> invocation.getArgument(0));

        var response = service.update(consultaId, request);

        assertThat(response.getStatus()).isEqualTo(AppointmentStatus.REMARCADA);
        assertThat(response.getDataHoraInicio()).isEqualTo(request.getDataHoraInicio());
        verify(publisher).publishChanged(any(AppointmentEntity.class));
    }
}


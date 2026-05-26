package br.com.fiap.techchallenge.hospital.notification.service;

import br.com.fiap.techchallenge.hospital.notification.repository.NotificationRepository;
import br.com.fiap.techchallenge.hospital.notification.repository.ProcessedEventRepository;
import br.com.fiap.techchallenge.hospital.shared.AppointmentStatus;
import br.com.fiap.techchallenge.hospital.shared.ConsultationCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class NotificationProcessorTest {

    private final NotificationRepository notificationRepository = mock(NotificationRepository.class);
    private final ProcessedEventRepository processedEventRepository = mock(ProcessedEventRepository.class);
    private final NotificationSender sender = mock(NotificationSender.class);
    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private final NotificationProcessor processor = new NotificationProcessor(
            notificationRepository,
            processedEventRepository,
            sender,
            objectMapper
    );

    @Test
    void deveEnviarLembreteParaConsultaFutura() {
        var event = event(AppointmentStatus.AGENDADA, OffsetDateTime.now().plusDays(2));

        processor.process(event);

        verify(sender).sendAppointmentReminder(event.pacienteId(), event.consultaId(), event.dataHoraInicio());
        verify(notificationRepository).save(any());
        verify(processedEventRepository).save(any());
    }

    @Test
    void deveSerIdempotenteQuandoEventoJaFoiProcessado() {
        var event = event(AppointmentStatus.AGENDADA, OffsetDateTime.now().plusDays(2));
        when(processedEventRepository.existsById(event.eventId())).thenReturn(true);

        processor.process(event);

        verify(sender, never()).sendAppointmentReminder(any(), any(), any());
        verify(notificationRepository, never()).save(any());
    }

    private ConsultationCreatedEvent event(AppointmentStatus status, OffsetDateTime dataHoraInicio) {
        return new ConsultationCreatedEvent(
                UUID.randomUUID(),
                ConsultationCreatedEvent.TYPE,
                OffsetDateTime.now(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                UUID.randomUUID(),
                dataHoraInicio,
                status
        );
    }
}


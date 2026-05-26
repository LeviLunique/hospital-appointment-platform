package br.com.fiap.techchallenge.hospital.history.service;

import br.com.fiap.techchallenge.hospital.history.domain.AppointmentHistoryEntity;
import br.com.fiap.techchallenge.hospital.history.repository.AppointmentHistoryRepository;
import br.com.fiap.techchallenge.hospital.shared.ConsultationChangedEvent;
import br.com.fiap.techchallenge.hospital.shared.ConsultationCreatedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppointmentHistoryProjector {

    private final AppointmentHistoryRepository historyRepository;
    private final ObjectMapper objectMapper;

    @Transactional
    @KafkaListener(topics = "${app.kafka.topics.consulta-criada}", groupId = "${spring.kafka.consumer.group-id}")
    public void onCreated(String payload) throws Exception {
        var event = objectMapper.readValue(payload, ConsultationCreatedEvent.class);
        upsert(AppointmentHistoryEntity.builder()
                .id(event.consultaId())
                .pacienteId(event.pacienteId())
                .medicoId(event.medicoId())
                .dataHoraInicio(event.dataHoraInicio())
                .status(event.status())
                .build());
        log.info("Historico atualizado para consulta criada {}", event.consultaId());
    }

    @Transactional
    @KafkaListener(topics = "${app.kafka.topics.consulta-alterada}", groupId = "${spring.kafka.consumer.group-id}")
    public void onChanged(String payload) throws Exception {
        var event = objectMapper.readValue(payload, ConsultationChangedEvent.class);
        upsert(AppointmentHistoryEntity.builder()
                .id(event.consultaId())
                .pacienteId(event.pacienteId())
                .medicoId(event.medicoId())
                .dataHoraInicio(event.dataHoraInicio())
                .status(event.status())
                .build());
        log.info("Historico atualizado para consulta alterada {}", event.consultaId());
    }

    private void upsert(AppointmentHistoryEntity incoming) {
        var target = historyRepository.findById(incoming.getId()).orElse(incoming);
        target.setPacienteId(incoming.getPacienteId());
        target.setMedicoId(incoming.getMedicoId());
        target.setDataHoraInicio(incoming.getDataHoraInicio());
        target.setStatus(incoming.getStatus());
        historyRepository.save(target);
    }
}


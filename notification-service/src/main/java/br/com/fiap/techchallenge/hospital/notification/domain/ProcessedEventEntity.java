package br.com.fiap.techchallenge.hospital.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "processed_events")
public class ProcessedEventEntity {

    @Id
    private UUID eventId;

    @Column(nullable = false)
    private OffsetDateTime processedAt;

    @PrePersist
    public void prePersist() {
        processedAt = OffsetDateTime.now();
    }
}


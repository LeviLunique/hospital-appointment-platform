package br.com.fiap.techchallenge.hospital.notification.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "notifications")
public class NotificationEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private UUID consultaId;

    @Column(nullable = false)
    private UUID pacienteId;

    @Column(nullable = false)
    private String tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationStatus status;

    @Column(columnDefinition = "text")
    private String payload;

    @Column(nullable = false)
    private int tentativas;

    @Column(nullable = false)
    private OffsetDateTime createdAt;

    private OffsetDateTime sentAt;

    @PrePersist
    public void prePersist() {
        createdAt = OffsetDateTime.now();
    }
}


package br.com.fiap.techchallenge.hospital.scheduling.service;

import java.util.UUID;

public class AppointmentNotFoundException extends RuntimeException {

    public AppointmentNotFoundException(UUID consultaId) {
        super("Consulta " + consultaId + " nao foi encontrada");
    }
}


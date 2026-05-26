package br.com.fiap.techchallenge.hospital.shared;

import lombok.experimental.UtilityClass;

@UtilityClass
public class KafkaTopics {
    public static final String CONSULTA_CRIADA = "hospital.consultas.criadas.v1";
    public static final String CONSULTA_ALTERADA = "hospital.consultas.alteradas.v1";
    public static final String CONSULTA_DLT = "hospital.consultas.dlt";
}


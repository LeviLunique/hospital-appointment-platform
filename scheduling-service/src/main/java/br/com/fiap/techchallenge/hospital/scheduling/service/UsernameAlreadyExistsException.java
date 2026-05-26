package br.com.fiap.techchallenge.hospital.scheduling.service;

public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException(String username) {
        super("Username ja cadastrado: " + username);
    }
}

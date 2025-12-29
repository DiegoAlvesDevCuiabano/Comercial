package com.controle_comercial.exception;

public class ClienteNotFoundException extends EntityNotFoundException {
    public ClienteNotFoundException(Integer id) {
        super("Cliente", id);
    }
}

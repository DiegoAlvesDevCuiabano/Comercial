package com.controle_comercial.exception;

public class LocalNotFoundException extends EntityNotFoundException {
    public LocalNotFoundException(Integer id) {
        super("Local", id);
    }
}

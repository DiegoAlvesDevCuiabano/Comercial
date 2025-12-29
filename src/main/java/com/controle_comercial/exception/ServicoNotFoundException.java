package com.controle_comercial.exception;

public class ServicoNotFoundException extends EntityNotFoundException {
    public ServicoNotFoundException(Integer id) {
        super("Serviço", id);
    }
}

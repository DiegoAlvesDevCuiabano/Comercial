package com.controle_comercial.model.dto;


public class UserDetailsDTO {
    private final String name;
    private final boolean isMaster;

    public UserDetailsDTO(String name, boolean isMaster) {
        this.name = name;
        this.isMaster = isMaster;
    }

    public String getName() {
        return name;
    }

    public boolean isMaster() {
        return isMaster;
    }
}

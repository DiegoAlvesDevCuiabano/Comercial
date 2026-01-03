package com.controle_comercial.exception;

public class EntityNotFoundException extends RuntimeException {
    private final String entityName;
    private final Integer entityId;

    public EntityNotFoundException(String entityName, Integer id) {
        super(String.format("%s com ID %d não encontrado", entityName, id));
        this.entityName = entityName;
        this.entityId = id;
    }

    public String getEntityName() {
        return entityName;
    }

    public Integer getEntityId() {
        return entityId;
    }
}

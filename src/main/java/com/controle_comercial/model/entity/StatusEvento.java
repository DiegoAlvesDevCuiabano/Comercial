package com.controle_comercial.model.entity;

public enum StatusEvento {
    PLANEJAMENTO("Planejamento", "primary"),
    CONFIRMADO("Confirmado", "success"),
    EM_ANDAMENTO("Em Andamento", "warning"),
    CONCLUIDO("Concluído", "secondary"),
    CANCELADO("Cancelado", "danger");

    private final String descricao;
    private final String bootstrapClass;

    StatusEvento(String descricao, String bootstrapClass) {
        this.descricao = descricao;
        this.bootstrapClass = bootstrapClass;
    }

    public String getDescricao() {
        return descricao;
    }

    public String getBootstrapClass() {
        return bootstrapClass;
    }
}

package com.controle_comercial.model.entity;

public enum StatusOrcamento {
    RASCUNHO("Rascunho", "secondary"),
    ENVIADO("Enviado", "info"),
    APROVADO("Aprovado", "success"),
    REJEITADO("Rejeitado", "danger"),
    EXPIRADO("Expirado", "warning"),
    CONVERTIDO("Convertido em Evento", "dark");

    private final String descricao;
    private final String bootstrapClass;

    StatusOrcamento(String descricao, String bootstrapClass) {
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

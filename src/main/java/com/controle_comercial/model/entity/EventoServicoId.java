package com.controle_comercial.model.entity;

import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class EventoServicoId implements Serializable {
    private Integer eventoId;
    private Integer servicoId;

    public EventoServicoId(Integer eventoId, Integer servicoId) {
        this.eventoId = eventoId;
        this.servicoId = servicoId;
    }

    public EventoServicoId() {
    }

    public Integer getEventoId() {
        return eventoId;
    }

    public void setEventoId(Integer eventoId) {
        this.eventoId = eventoId;
    }

    public Integer getServicoId() {
        return servicoId;
    }

    public void setServicoId(Integer servicoId) {
        this.servicoId = servicoId;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof EventoServicoId that)) return false;
        return Objects.equals(eventoId, that.eventoId) && Objects.equals(servicoId, that.servicoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(eventoId, servicoId);
    }
}
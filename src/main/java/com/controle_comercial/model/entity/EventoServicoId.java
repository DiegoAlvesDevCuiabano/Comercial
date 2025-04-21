package com.controle_comercial.model.entity;

import java.io.Serializable;
import java.util.Objects;

public class EventoServicoId implements Serializable {
    private Integer evento;
    private Integer servico;

    public EventoServicoId() {}

    public EventoServicoId(Integer evento, Integer servico) {
        this.evento = evento;
        this.servico = servico;
    }

    public Integer getEvento() {
        return evento;
    }

    public void setEvento(Integer evento) {
        this.evento = evento;
    }

    public Integer getServico() {
        return servico;
    }

    public void setServico(Integer servico) {
        this.servico = servico;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventoServicoId that = (EventoServicoId) o;
        return Objects.equals(evento, that.evento) && Objects.equals(servico, that.servico);
    }

    @Override
    public int hashCode() {
        return Objects.hash(evento, servico);
    }
}

package com.controle_comercial.model.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
@Table(name = "evento_servico")
public class EventoServico {

    @EmbeddedId
    private EventoServicoId id;

    @ManyToOne
    @MapsId("eventoId")
    @JoinColumn(name = "id_evento")
    private Evento evento;

    @ManyToOne
    @JoinColumn(name = "id_servico")
    @JsonBackReference
    private Servico servico;

    @Column(nullable = false)
    private Integer quantidade;

    public EventoServicoId getId() {
        return id;
    }

    public void setId(EventoServicoId id) {
        this.id = id;
    }

    public Evento getEvento() {
        return evento;
    }

    public void setEvento(Evento evento) {
        this.evento = evento;
    }

    public Servico getServico() {
        return servico;
    }

    public void setServico(Servico servico) {
        this.servico = servico;
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }
}
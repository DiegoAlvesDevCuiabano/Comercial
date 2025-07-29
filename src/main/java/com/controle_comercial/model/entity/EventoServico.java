package com.controle_comercial.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "evento_servico")
public class EventoServico {

    @EmbeddedId
    private EventoServicoId id;

    @ManyToOne
    @MapsId("eventoId")
    @JoinColumn(name = "id_evento")
    @JsonIgnore
    private Evento evento;

    @ManyToOne
    @JoinColumn(name = "id_servico")
    @JsonIgnoreProperties("eventos")
    private Servico servico;

    @Column(nullable = false)
    private Integer quantidade;
}
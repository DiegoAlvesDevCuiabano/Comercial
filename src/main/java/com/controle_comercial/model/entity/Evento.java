package com.controle_comercial.model.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
@Entity
@Table(name = "eventos")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Geração automática de ID
    private Long id;

    @Column(nullable = false)
    private String titulo;

    @Column(nullable = false)
    private LocalDate data; // Data do evento

    private String descricao; // Descrição opcional do evento

    @Column(nullable = true)
    private String local; // Local do evento (opcional)
}

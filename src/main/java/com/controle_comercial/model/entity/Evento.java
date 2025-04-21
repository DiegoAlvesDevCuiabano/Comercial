package com.controle_comercial.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@Entity
@Table(name = "Evento")
public class Evento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_evento")
    private Integer idEvento;

    @Column(nullable = false, length = 100)
    private String titulo;

    @Column(nullable = false, name = "data_evento")
    private LocalDate dataEvento;

    @Column(nullable = false, name = "hora_inicio")
    private LocalTime horaInicio;

    @Column(nullable = false, name = "hora_fim")
    private LocalTime horaFim;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_local", nullable = false)
    private Local local;

    @Column(name = "valor_total", columnDefinition = "DECIMAL(10,2)")
    private Double valorTotal;

    @Column(columnDefinition = "TEXT")
    private String observacoes;
}

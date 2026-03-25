package com.controle_comercial.model.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Servico")
public class Servico {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_servico")
    private Integer idServico;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(nullable = false, name = "preco_unitario", precision = 10, scale = 2)
    private BigDecimal precoUnitario;

    @OneToMany(mappedBy = "servico", cascade = CascadeType.ALL)
    private Set<EventoServico> eventos = new HashSet<>();
}
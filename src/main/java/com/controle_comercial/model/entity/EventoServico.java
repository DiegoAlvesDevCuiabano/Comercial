package com.controle_comercial.model.entity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@IdClass(EventoServicoId.class)
@Table(name = "Evento_Servico")
public class EventoServico {

    @Id
    @ManyToOne
    @JoinColumn(name = "id_evento", nullable = false)
    private Evento evento;

    @Id
    @ManyToOne
    @JoinColumn(name = "id_servico", nullable = false)
    private Servico servico;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false, precision = 10, name = "preco_unitario", columnDefinition = "DECIMAL(10,2)")
    private Double precoUnitario;
}

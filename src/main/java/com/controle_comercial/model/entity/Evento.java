package com.controle_comercial.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

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

    @Column(nullable = false, name = "data_inicio")
    private LocalDate dataInicio;

    @Column(nullable = false, name = "data_fim")
    private LocalDate dataFim;

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

    @Column(name = "valor_total", precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "desconto_valor", precision = 10, scale = 2)
    private BigDecimal descontoValor;

    @Column(name = "desconto_percentual", precision = 5, scale = 2)
    private BigDecimal descontoPercentual;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @OneToMany(mappedBy = "evento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("evento")
    private Set<EventoServico> servicos = new HashSet<>();

    public void adicionarServico(Servico servico, Integer quantidade) {
        EventoServico eventoServico = new EventoServico();
        eventoServico.setEvento(this);
        eventoServico.setServico(servico);
        eventoServico.setQuantidade(quantidade);

        EventoServicoId id = new EventoServicoId();
        id.setEventoId(this.getIdEvento());
        id.setServicoId(servico.getIdServico());
        eventoServico.setId(id);

        servicos.add(eventoServico);
    }


}
package com.controle_comercial.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "OrcamentoServico")
public class OrcamentoServico {

    @EmbeddedId
    private OrcamentoServicoId id;

    @ManyToOne
    @MapsId("orcamentoId")
    @JoinColumn(name = "id_orcamento")
    @JsonIgnore
    private Orcamento orcamento;

    @ManyToOne
    @MapsId("servicoId")
    @JoinColumn(name = "id_servico")
    @JsonIgnoreProperties("eventos")
    private Servico servico;

    @Column(nullable = false)
    private Integer quantidade;
}

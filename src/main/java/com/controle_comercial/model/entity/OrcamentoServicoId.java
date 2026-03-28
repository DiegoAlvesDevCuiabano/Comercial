package com.controle_comercial.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class OrcamentoServicoId implements Serializable {

    @Column(name = "id_orcamento")
    private Integer orcamentoId;

    @Column(name = "id_servico")
    private Integer servicoId;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrcamentoServicoId that = (OrcamentoServicoId) o;
        return Objects.equals(orcamentoId, that.orcamentoId) && Objects.equals(servicoId, that.servicoId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(orcamentoId, servicoId);
    }
}

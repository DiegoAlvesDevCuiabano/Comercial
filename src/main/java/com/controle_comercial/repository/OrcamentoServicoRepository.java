package com.controle_comercial.repository;

import com.controle_comercial.model.entity.OrcamentoServico;
import com.controle_comercial.model.entity.OrcamentoServicoId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface OrcamentoServicoRepository extends JpaRepository<OrcamentoServico, OrcamentoServicoId> {

    @Modifying
    @Query("DELETE FROM OrcamentoServico os WHERE os.orcamento.idOrcamento = :orcamentoId")
    void deleteByOrcamentoId(@Param("orcamentoId") Integer orcamentoId);
}

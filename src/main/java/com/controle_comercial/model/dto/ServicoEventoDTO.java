package com.controle_comercial.model.dto;

import com.controle_comercial.model.entity.EventoServico;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class ServicoEventoDTO {

    private ServicoResumoDTO servico;
    private Integer quantidade;

    public static ServicoEventoDTO fromEntity(EventoServico eventoServico) {
        ServicoEventoDTO dto = new ServicoEventoDTO();
        dto.setQuantidade(eventoServico.getQuantidade());

        if (eventoServico.getServico() != null) {
            ServicoResumoDTO servicoDTO = new ServicoResumoDTO();
            servicoDTO.setIdServico(eventoServico.getServico().getIdServico());
            servicoDTO.setNome(eventoServico.getServico().getNome());
            servicoDTO.setPrecoUnitario(eventoServico.getServico().getPrecoUnitario());
            dto.setServico(servicoDTO);
        }

        return dto;
    }

    @Getter
    @Setter
    public static class ServicoResumoDTO {
        private Integer idServico;
        private String nome;
        private BigDecimal precoUnitario;
    }
}

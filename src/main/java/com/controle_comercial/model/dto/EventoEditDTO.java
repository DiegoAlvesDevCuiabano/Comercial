package com.controle_comercial.model.dto;

import com.controle_comercial.model.entity.Evento;
import com.controle_comercial.model.entity.StatusEvento;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class EventoEditDTO {

    private Integer idEvento;
    private String titulo;
    private String dataInicio;
    private String dataFim;
    private String horaInicio;
    private String horaFim;
    private BigDecimal valorTotal;
    private BigDecimal descontoValor;
    private BigDecimal descontoPercentual;
    private StatusEvento status;
    private String locaisAdicionais;
    private Integer estimativaPublico;
    private String observacoes;
    private ClienteResumoDTO cliente;
    private LocalResumoDTO local;
    private List<ServicoEventoDTO> servicos;

    public static EventoEditDTO fromEntity(Evento evento) {
        EventoEditDTO dto = new EventoEditDTO();
        dto.setIdEvento(evento.getIdEvento());
        dto.setTitulo(evento.getTitulo());
        dto.setDataInicio(evento.getDataInicio().toString());
        dto.setDataFim(evento.getDataFim().toString());
        dto.setHoraInicio(evento.getHoraInicio().toString());
        dto.setHoraFim(evento.getHoraFim().toString());
        dto.setValorTotal(evento.getValorTotal());
        dto.setDescontoValor(evento.getDescontoValor());
        dto.setDescontoPercentual(evento.getDescontoPercentual());
        dto.setStatus(evento.getStatus());
        dto.setLocaisAdicionais(evento.getLocaisAdicionais());
        dto.setEstimativaPublico(evento.getEstimativaPublico());
        dto.setObservacoes(evento.getObservacoes());

        if (evento.getCliente() != null) {
            dto.setCliente(ClienteResumoDTO.fromEntity(evento.getCliente()));
        }

        if (evento.getLocal() != null) {
            dto.setLocal(LocalResumoDTO.fromEntity(evento.getLocal()));
        }

        if (evento.getServicos() != null && !evento.getServicos().isEmpty()) {
            dto.setServicos(evento.getServicos().stream()
                    .map(ServicoEventoDTO::fromEntity)
                    .collect(Collectors.toList()));
        }

        return dto;
    }
}

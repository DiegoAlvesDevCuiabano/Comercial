package com.controle_comercial.model.dto;

import com.controle_comercial.model.entity.Cliente;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ClienteResumoDTO {

    private Integer idCliente;
    private String nome;

    public static ClienteResumoDTO fromEntity(Cliente cliente) {
        ClienteResumoDTO dto = new ClienteResumoDTO();
        dto.setIdCliente(cliente.getIdCliente());
        dto.setNome(cliente.getNome());
        return dto;
    }
}

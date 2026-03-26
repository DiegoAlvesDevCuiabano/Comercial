package com.controle_comercial.model.dto;

import com.controle_comercial.model.entity.Local;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LocalResumoDTO {

    private Integer idLocal;
    private String nome;

    public static LocalResumoDTO fromEntity(Local local) {
        LocalResumoDTO dto = new LocalResumoDTO();
        dto.setIdLocal(local.getIdLocal());
        dto.setNome(local.getNome());
        return dto;
    }
}

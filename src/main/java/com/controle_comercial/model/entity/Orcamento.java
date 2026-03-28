package com.controle_comercial.model.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "Orcamento")
public class Orcamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_orcamento")
    private Integer idOrcamento;

    @Column(name = "numero_orcamento", nullable = false, unique = true, length = 20)
    private String numeroOrcamento;

    @Column(name = "data_validade")
    private LocalDate dataValidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusOrcamento status = StatusOrcamento.RASCUNHO;

    @ManyToOne
    @JoinColumn(name = "id_cliente", nullable = false)
    private Cliente cliente;

    @ManyToOne
    @JoinColumn(name = "id_local", nullable = false)
    private Local local;

    @Column(name = "data_inicio_evento", nullable = false)
    private LocalDate dataInicioEvento;

    @Column(name = "data_fim_evento", nullable = false)
    private LocalDate dataFimEvento;

    @Column(name = "hora_inicio", nullable = false)
    private LocalTime horaInicio;

    @Column(name = "hora_fim", nullable = false)
    private LocalTime horaFim;

    @OneToMany(mappedBy = "orcamento", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("orcamento")
    private Set<OrcamentoServico> servicos = new HashSet<>();

    @Column(name = "valor_total", precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "desconto_valor", precision = 10, scale = 2)
    private BigDecimal descontoValor;

    @Column(name = "desconto_percentual", precision = 5, scale = 2)
    private BigDecimal descontoPercentual;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(name = "data_criacao", nullable = false)
    private LocalDateTime dataCriacao = LocalDateTime.now();

    @Column(name = "data_envio")
    private LocalDateTime dataEnvio;

    @Column(name = "data_resposta")
    private LocalDateTime dataResposta;

    public void adicionarServico(Servico servico, Integer quantidade) {
        OrcamentoServico os = new OrcamentoServico();
        os.setOrcamento(this);
        os.setServico(servico);
        os.setQuantidade(quantidade);

        OrcamentoServicoId id = new OrcamentoServicoId();
        id.setOrcamentoId(this.getIdOrcamento());
        id.setServicoId(servico.getIdServico());
        os.setId(id);

        servicos.add(os);
    }
}

package AgroBoi.domain.entity;

import java.time.LocalDate;

import AgroBoi.domain.enums.ResultadoReproducao;
import AgroBoi.domain.enums.TipoReproducao;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reproducao")
@Getter @Setter @NoArgsConstructor
public class Reproducao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "matriz_id", nullable = false)
    private Animal matriz;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reprodutor_id")
    private Animal reprodutor;

    @Column(name = "referencia_reprodutor_externo", length = 150)
    private String referenciaReprodutorExterno;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private TipoReproducao tipo;

    @Column(name = "data_evento", nullable = false)
    private LocalDate dataEvento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResultadoReproducao resultado = ResultadoReproducao.PENDENTE;

    @Column(name = "data_prevista_parto")
    private LocalDate dataPrevistaParto;

    @Column(name = "data_desfecho")
    private LocalDate dataDesfecho;

    @Column(columnDefinition = "TEXT")
    private String observacoes;
}
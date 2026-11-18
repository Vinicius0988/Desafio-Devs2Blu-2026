package AgroBoi.domain.entity;

import java.time.LocalDate;

import AgroBoi.domain.enums.MotivoMovimentacao;
import AgroBoi.domain.enums.TipoMovimentacao;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "movimentacao")
@Getter @Setter @NoArgsConstructor
public class Movimentacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 7)
    private TipoMovimentacao tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private MotivoMovimentacao motivo;

    @Column(nullable = false)
    private LocalDate data;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_venda_id", unique = true)
    private Itemvenda itemVenda;

    @Column(name = "origem_destino", length = 255)
    private String origemDestino;

    @Column(columnDefinition = "TEXT")
    private String observacoes;
}
package AgroBoi.demo.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import AgroBoi.demo.domain.enums.CategoriaDespesa;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "despesa")
@Getter @Setter @NoArgsConstructor
public class Despesa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id")
    private Animal animal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private CategoriaDespesa categoria;

    @Column(nullable = false, length = 255)
    private String descricao;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal valor;

    @Column(columnDefinition = "TEXT")
    private String observacoes;
}
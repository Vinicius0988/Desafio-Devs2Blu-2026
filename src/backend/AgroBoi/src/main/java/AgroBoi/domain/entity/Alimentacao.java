package AgroBoi.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "alimentacao")
@Getter @Setter @NoArgsConstructor
public class Alimentacao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "animal_id")   // opcional: pode ser para o rebanho todo
    private Animal animal;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false, length = 255)
    private String descricao;

    @Column(precision = 12, scale = 3)
    private BigDecimal quantidade;

    @Column(length = 20)
    private String unidade;

    @Column(columnDefinition = "TEXT")
    private String observacoes;
}
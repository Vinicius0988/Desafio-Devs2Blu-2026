package AgroBoi.demo.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;

import AgroBoi.demo.domain.enums.TipoSanitario;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "registro_sanitario")
@Getter @Setter @NoArgsConstructor
public class RegistroSanitario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private TipoSanitario tipo;

    @Column(name = "data_aplicacao", nullable = false)
    private LocalDate dataAplicacao;

    @Column(nullable = false, length = 255)
    private String descricao;

    @Column(precision = 10, scale = 3)
    private BigDecimal dose;

    @Column(name = "unidade_dose", length = 20)
    private String unidadeDose;

    @Column(name = "proxima_aplicacao")
    private LocalDate proximaAplicacao;

    @Column(columnDefinition = "TEXT")
    private String observacoes;
}
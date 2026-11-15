package AgroBoi.demo.domain.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.hibernate.annotations.CreationTimestamp;

import AgroBoi.demo.domain.enums.Sexo;
import AgroBoi.demo.domain.enums.StatusAnimal;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "animal")
@Getter
@Setter
@NoArgsConstructor
public class Animal {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String identificacao;

    @Column(length = 100)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 5)
    private Sexo sexo;

    @Column(name = "data_nascimento")
    private LocalDate dataNascimento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "raca_id")
    private Raca raca;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mae_id")
    private Animal mae;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pai_id")
    private Animal pai;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 15)
    private StatusAnimal situacao = StatusAnimal.NA_FAZENDA;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @CreationTimestamp
    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Transient
    public Integer getIdadeEmMeses() {
        return dataNascimento == null ? null
                : (int) ChronoUnit.MONTHS.between(dataNascimento, LocalDate.now());
    }
}
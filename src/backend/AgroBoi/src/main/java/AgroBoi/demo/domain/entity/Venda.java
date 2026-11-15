package AgroBoi.demo.domain.entity;

import java.time.LocalDate;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "venda")
@Getter @Setter @NoArgsConstructor
public class Venda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false, length = 150)
    private String comprador;

    @Column(columnDefinition = "TEXT")
    private String observacoes;
}
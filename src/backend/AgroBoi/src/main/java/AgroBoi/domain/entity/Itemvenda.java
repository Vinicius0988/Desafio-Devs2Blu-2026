package AgroBoi.domain.entity;

import java.math.BigDecimal;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "item_venda",
       uniqueConstraints = @UniqueConstraint(columnNames = {"venda_id", "animal_id"}))
@Getter @Setter @NoArgsConstructor
public class Itemvenda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "venda_id", nullable = false)
    private Venda venda;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "animal_id", nullable = false)
    private Animal animal;

    @Column(nullable = false, precision = 14, scale = 2)
    private BigDecimal valor;
}
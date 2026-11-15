package AgroBoi.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "raca")
@Getter @Setter @NoArgsConstructor
public class Raca {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nome;
}
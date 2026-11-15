package AgroBoi.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "fazenda")
@Getter @Setter @NoArgsConstructor
public class Fazenda {
    @Id
    private Long id = 1L;   // fixo em 1, sem @GeneratedValue

    @Column(nullable = false, length = 150)
    private String nome;

    @Column(name = "nome_responsavel", nullable = false, length = 150)
    private String nomeResponsavel;

    @Column(length = 100)
    private String municipio;

    @Column(length = 2, columnDefinition = "CHAR(2)")
    private String uf;

    @Column(length = 255)
    private String endereco;
}
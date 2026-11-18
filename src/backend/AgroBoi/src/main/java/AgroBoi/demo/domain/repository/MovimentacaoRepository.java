package AgroBoi.demo.domain.repository;

import AgroBoi.demo.domain.entity.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
}

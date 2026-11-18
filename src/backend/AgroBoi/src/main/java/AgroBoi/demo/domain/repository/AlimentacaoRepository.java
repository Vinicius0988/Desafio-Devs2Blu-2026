package AgroBoi.demo.domain.repository;

import AgroBoi.demo.domain.entity.Alimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlimentacaoRepository extends JpaRepository<Alimentacao, Long> {
}

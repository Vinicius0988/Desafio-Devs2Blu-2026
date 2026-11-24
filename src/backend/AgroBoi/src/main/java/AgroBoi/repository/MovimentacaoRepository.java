package AgroBoi.repository;

import AgroBoi.domain.entity.Movimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MovimentacaoRepository extends JpaRepository<Movimentacao, Long> {
    java.util.List<Movimentacao> findByAnimalIdOrderByDataAscIdAsc(Long animalId);
}

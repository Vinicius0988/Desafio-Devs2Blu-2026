package AgroBoi.demo.domain.repository;

import AgroBoi.demo.domain.entity.Despesa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DespesaRepository extends JpaRepository<Despesa, Long> {
}

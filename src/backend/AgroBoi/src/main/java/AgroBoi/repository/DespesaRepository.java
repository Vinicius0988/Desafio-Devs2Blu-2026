package AgroBoi.repository;

import AgroBoi.domain.entity.Despesa;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DespesaRepository extends JpaRepository<Despesa, Long> {
    java.util.List<Despesa> findByDataBetween(java.time.LocalDate inicio, java.time.LocalDate fim);
}

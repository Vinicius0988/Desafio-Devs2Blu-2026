package AgroBoi.repository;

import AgroBoi.domain.entity.Fazenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FazendaRepository extends JpaRepository<Fazenda, Long> {
}

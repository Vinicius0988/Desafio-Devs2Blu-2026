package AgroBoi.demo.domain.repository;

import AgroBoi.demo.domain.entity.Fazenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FazendaRepository extends JpaRepository<Fazenda, Long> {
}

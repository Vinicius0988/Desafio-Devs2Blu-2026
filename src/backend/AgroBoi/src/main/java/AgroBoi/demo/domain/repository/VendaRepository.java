package AgroBoi.demo.domain.repository;

import AgroBoi.demo.domain.entity.Venda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VendaRepository extends JpaRepository<Venda, Long> {
}

package AgroBoi.demo.domain.repository;

import AgroBoi.demo.domain.entity.Pesagem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PesagemRepository extends JpaRepository<Pesagem, Long> {
}

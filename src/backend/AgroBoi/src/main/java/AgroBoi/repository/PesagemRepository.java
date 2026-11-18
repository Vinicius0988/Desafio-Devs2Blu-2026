package AgroBoi.repository;

import AgroBoi.domain.entity.Pesagem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PesagemRepository extends JpaRepository<Pesagem, Long> {
}

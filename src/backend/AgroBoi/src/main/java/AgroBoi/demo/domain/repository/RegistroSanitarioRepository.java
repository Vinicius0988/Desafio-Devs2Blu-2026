package AgroBoi.demo.domain.repository;

import AgroBoi.demo.domain.entity.RegistroSanitario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroSanitarioRepository extends JpaRepository<RegistroSanitario, Long> {
}

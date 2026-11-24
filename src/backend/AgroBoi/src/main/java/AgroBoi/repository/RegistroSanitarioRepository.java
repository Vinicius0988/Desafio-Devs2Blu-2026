package AgroBoi.repository;

import AgroBoi.domain.entity.RegistroSanitario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroSanitarioRepository extends JpaRepository<RegistroSanitario, Long> {
    boolean existsByAnimalIdAndDataAplicacaoBefore(Long animalId, java.time.LocalDate data);
    boolean existsByAnimalIdAndDataAplicacaoAfter(Long animalId, java.time.LocalDate data);
}

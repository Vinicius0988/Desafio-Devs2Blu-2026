package AgroBoi.repository;

import AgroBoi.domain.entity.RegistroSanitario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistroSanitarioRepository extends JpaRepository<RegistroSanitario, Long> {
    long countByProximaAplicacaoBefore(java.time.LocalDate data);
    long countByProximaAplicacaoBetween(java.time.LocalDate inicio, java.time.LocalDate fim);
    boolean existsByAnimalIdAndDataAplicacaoBefore(Long animalId, java.time.LocalDate data);
    boolean existsByAnimalIdAndDataAplicacaoAfter(Long animalId, java.time.LocalDate data);
}

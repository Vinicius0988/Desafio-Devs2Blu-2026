package AgroBoi.repository;

import AgroBoi.domain.entity.Alimentacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlimentacaoRepository extends JpaRepository<Alimentacao, Long> {
    boolean existsByAnimalIdAndDataBefore(Long animalId, java.time.LocalDate data);
    boolean existsByAnimalIdAndDataAfter(Long animalId, java.time.LocalDate data);
}

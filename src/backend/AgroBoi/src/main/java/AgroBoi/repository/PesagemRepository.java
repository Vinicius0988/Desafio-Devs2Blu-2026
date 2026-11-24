package AgroBoi.repository;

import AgroBoi.domain.entity.Pesagem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PesagemRepository extends JpaRepository<Pesagem, Long> {
    boolean existsByAnimalIdAndDataHoraBefore(Long animalId, java.time.LocalDateTime data);
    boolean existsByAnimalIdAndDataHoraAfter(Long animalId, java.time.LocalDateTime data);
}

package AgroBoi.repository;

import AgroBoi.domain.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
    java.util.Optional<Animal> findByIdentificacaoIgnoreCase(String identificacao);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select a from Animal a where a.id = :id")
    java.util.Optional<Animal> buscarComBloqueio(@org.springframework.data.repository.query.Param("id") Long id);
}

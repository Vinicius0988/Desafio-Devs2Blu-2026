package AgroBoi.demo.domain.repository;

import AgroBoi.demo.domain.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
}

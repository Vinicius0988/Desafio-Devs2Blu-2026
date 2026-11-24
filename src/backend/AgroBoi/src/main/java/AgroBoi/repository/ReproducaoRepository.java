package AgroBoi.repository;

import AgroBoi.domain.entity.Reproducao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReproducaoRepository extends JpaRepository<Reproducao, Long> {
    @org.springframework.data.jpa.repository.Query("select count(r) > 0 from Reproducao r where (r.matriz.id = :id and (r.dataEvento < :data or r.dataDesfecho < :data)) or (r.reprodutor.id = :id and r.dataEvento < :data)")
    boolean existeEventoAntes(@org.springframework.data.repository.query.Param("id") Long id, @org.springframework.data.repository.query.Param("data") java.time.LocalDate data);
    @org.springframework.data.jpa.repository.Query("select count(r) > 0 from Reproducao r where (r.matriz.id = :id and (r.dataEvento > :data or r.dataDesfecho > :data)) or (r.reprodutor.id = :id and r.dataEvento > :data)")
    boolean existeEventoDepois(@org.springframework.data.repository.query.Param("id") Long id, @org.springframework.data.repository.query.Param("data") java.time.LocalDate data);
}

package AgroBoi.repository;

import AgroBoi.domain.entity.Animal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnimalRepository extends JpaRepository<Animal, Long> {
    java.util.Optional<Animal> findByIdentificacaoIgnoreCase(String identificacao);
    long countBySituacao(AgroBoi.domain.enums.StatusAnimal situacao);
    long countBySituacaoAndSexo(AgroBoi.domain.enums.StatusAnimal situacao, AgroBoi.domain.enums.Sexo sexo);
    long countByDataNascimentoBetween(java.time.LocalDate inicio, java.time.LocalDate fim);
    java.util.List<Animal> findBySituacao(AgroBoi.domain.enums.StatusAnimal situacao);
    /** Pesquisa (RF15) por identificação, nome ou nome da raça, sem diferenciar maiúsculas. */
    @org.springframework.data.jpa.repository.Query("select a from Animal a left join a.raca r where lower(a.identificacao) like lower(concat('%', :busca, '%')) or lower(coalesce(a.nome, '')) like lower(concat('%', :busca, '%')) or lower(coalesce(r.nome, '')) like lower(concat('%', :busca, '%')) order by a.identificacao")
    java.util.List<Animal> pesquisar(@org.springframework.data.repository.query.Param("busca") String busca);
    @org.springframework.data.jpa.repository.Lock(jakarta.persistence.LockModeType.PESSIMISTIC_WRITE)
    @org.springframework.data.jpa.repository.Query("select a from Animal a where a.id = :id")
    java.util.Optional<Animal> buscarComBloqueio(@org.springframework.data.repository.query.Param("id") Long id);
}

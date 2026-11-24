package AgroBoi.repository;

import AgroBoi.domain.entity.Itemvenda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemVendaRepository extends JpaRepository<Itemvenda, Long> {
    java.util.List<Itemvenda> findByVendaId(Long vendaId);
    java.util.List<Itemvenda> findByVendaDataBetween(java.time.LocalDate inicio, java.time.LocalDate fim);
}

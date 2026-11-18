package AgroBoi.demo.domain.dto.request;

import java.time.LocalDate;
import java.util.List;

public record VendaRequest(
        LocalDate data,
        String comprador,
        String observacoes,
        List<ItemVendaRequest> itens
) {
}

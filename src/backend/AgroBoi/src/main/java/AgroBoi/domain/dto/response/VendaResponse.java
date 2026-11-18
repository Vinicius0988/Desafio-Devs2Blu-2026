package AgroBoi.demo.domain.dto.response;

import java.time.LocalDate;
import java.util.List;

public record VendaResponse(
        Long id,
        LocalDate data,
        String comprador,
        String observacoes,
        List<ItemVendaResponse> itens
) {
}

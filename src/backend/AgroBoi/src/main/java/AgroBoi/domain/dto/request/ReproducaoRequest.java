package AgroBoi.domain.dto.request;

import AgroBoi.domain.enums.ResultadoReproducao;
import AgroBoi.domain.enums.TipoReproducao;
import java.time.LocalDate;

public record ReproducaoRequest(
        Long matrizId,
        Long reprodutorId,
        String referenciaReprodutorExterno,
        TipoReproducao tipo,
        LocalDate dataEvento,
        ResultadoReproducao resultado,
        LocalDate dataPrevistaParto,
        LocalDate dataDesfecho,
        String observacoes
) {
}

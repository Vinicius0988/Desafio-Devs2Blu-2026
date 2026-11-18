package AgroBoi.demo.domain.dto.request;

import AgroBoi.demo.domain.enums.ResultadoReproducao;
import AgroBoi.demo.domain.enums.TipoReproducao;
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

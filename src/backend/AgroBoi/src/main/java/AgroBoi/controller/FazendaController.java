package AgroBoi.controller;

import AgroBoi.domain.dto.request.FazendaRequest;
import AgroBoi.domain.dto.response.FazendaResponse;
import AgroBoi.mapper.Mapeador;
import AgroBoi.service.FazendaService;
import org.springframework.web.bind.annotation.*;

/** A instalação tem uma única fazenda (ID 1): GET lê, PUT cria ou atualiza. */
@RestController
@RequestMapping("/api/fazenda")
public class FazendaController {
    private final FazendaService service;

    public FazendaController(FazendaService service) {
        this.service = service;
    }

    @GetMapping
    public FazendaResponse buscar() {
        return service.buscarPorId(1L).map(Mapeador::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Fazenda ainda não cadastrada."));
    }

    @PutMapping
    public FazendaResponse salvar(@RequestBody FazendaRequest request) {
        return Mapeador.toResponse(service.salvar(Mapeador.toEntity(request)));
    }
}

package AgroBoi.controller;

import AgroBoi.domain.dto.request.RacaRequest;
import AgroBoi.domain.dto.response.RacaResponse;
import AgroBoi.domain.entity.Raca;
import AgroBoi.mapper.Mapeador;
import AgroBoi.service.RacaService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/racas")
public class RacaController {
    private final RacaService service;

    public RacaController(RacaService service) {
        this.service = service;
    }

    @GetMapping
    public List<RacaResponse> listar() {
        return service.listarTodos().stream()
                .map(Mapeador::toResponse)
                .toList();
    }

    @GetMapping("/{id}")
    public RacaResponse buscar(@PathVariable("id") Long id) {
        return service.buscarPorId(id).map(Mapeador::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Raça não encontrada."));
    }

    @PostMapping
    public ResponseEntity<RacaResponse> criar(@RequestBody RacaRequest request) {
        Raca salvo = service.salvar(Mapeador.toEntity(request, null));
        return ResponseEntity.created(URI.create("/api/racas/" + salvo.getId())).body(Mapeador.toResponse(salvo));
    }

    @PutMapping("/{id}")
    public RacaResponse atualizar(@PathVariable("id") Long id, @RequestBody RacaRequest request) {
        buscar(id); // 404 se não existir
        return Mapeador.toResponse(service.salvar(Mapeador.toEntity(request, id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        buscar(id); // 404 se não existir
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

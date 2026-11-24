package AgroBoi.controller;

import AgroBoi.domain.dto.request.DespesaRequest;
import AgroBoi.domain.dto.response.DespesaResponse;
import AgroBoi.domain.entity.Despesa;
import AgroBoi.mapper.Mapeador;
import AgroBoi.service.DespesaService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/despesas")
public class DespesaController {
    private final DespesaService service;

    public DespesaController(DespesaService service) {
        this.service = service;
    }

    @GetMapping
    public List<DespesaResponse> listar(@RequestParam(name = "animalId", required = false) Long animalId) {
        return service.listarTodos().stream()
                .map(Mapeador::toResponse)
                .filter(r -> animalId == null || animalId.equals(r.animalId()))
                .toList();
    }

    @GetMapping("/{id}")
    public DespesaResponse buscar(@PathVariable("id") Long id) {
        return service.buscarPorId(id).map(Mapeador::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Despesa não encontrada."));
    }

    @PostMapping
    public ResponseEntity<DespesaResponse> criar(@RequestBody DespesaRequest request) {
        Despesa salvo = service.salvar(Mapeador.toEntity(request, null));
        return ResponseEntity.created(URI.create("/api/despesas/" + salvo.getId())).body(Mapeador.toResponse(salvo));
    }

    @PutMapping("/{id}")
    public DespesaResponse atualizar(@PathVariable("id") Long id, @RequestBody DespesaRequest request) {
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

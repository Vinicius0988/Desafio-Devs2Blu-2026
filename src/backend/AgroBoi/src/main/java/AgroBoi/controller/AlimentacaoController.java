package AgroBoi.controller;

import AgroBoi.domain.dto.request.AlimentacaoRequest;
import AgroBoi.domain.dto.response.AlimentacaoResponse;
import AgroBoi.domain.entity.Alimentacao;
import AgroBoi.mapper.Mapeador;
import AgroBoi.service.AlimentacaoService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/alimentacoes")
public class AlimentacaoController {
    private final AlimentacaoService service;

    public AlimentacaoController(AlimentacaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<AlimentacaoResponse> listar(@RequestParam(name = "animalId", required = false) Long animalId) {
        return service.listarTodos().stream()
                .map(Mapeador::toResponse)
                .filter(r -> animalId == null || animalId.equals(r.animalId()))
                .toList();
    }

    @GetMapping("/{id}")
    public AlimentacaoResponse buscar(@PathVariable("id") Long id) {
        return service.buscarPorId(id).map(Mapeador::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro de alimentação não encontrado."));
    }

    @PostMapping
    public ResponseEntity<AlimentacaoResponse> criar(@RequestBody AlimentacaoRequest request) {
        Alimentacao salvo = service.salvar(Mapeador.toEntity(request, null));
        return ResponseEntity.created(URI.create("/api/alimentacoes/" + salvo.getId())).body(Mapeador.toResponse(salvo));
    }

    @PutMapping("/{id}")
    public AlimentacaoResponse atualizar(@PathVariable("id") Long id, @RequestBody AlimentacaoRequest request) {
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

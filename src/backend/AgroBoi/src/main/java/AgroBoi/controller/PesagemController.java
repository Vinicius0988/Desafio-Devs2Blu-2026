package AgroBoi.controller;

import AgroBoi.domain.dto.request.PesagemRequest;
import AgroBoi.domain.dto.response.PesagemResponse;
import AgroBoi.domain.entity.Pesagem;
import AgroBoi.mapper.Mapeador;
import AgroBoi.service.PesagemService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/pesagens")
public class PesagemController {
    private final PesagemService service;

    public PesagemController(PesagemService service) {
        this.service = service;
    }

    @GetMapping
    public List<PesagemResponse> listar(@RequestParam(name = "animalId", required = false) Long animalId) {
        return service.listarTodos().stream()
                .map(Mapeador::toResponse)
                .filter(r -> animalId == null || animalId.equals(r.animalId()))
                .toList();
    }

    @GetMapping("/{id}")
    public PesagemResponse buscar(@PathVariable("id") Long id) {
        return service.buscarPorId(id).map(Mapeador::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Pesagem não encontrada."));
    }

    @PostMapping
    public ResponseEntity<PesagemResponse> criar(@RequestBody PesagemRequest request) {
        Pesagem salvo = service.salvar(Mapeador.toEntity(request, null));
        return ResponseEntity.created(URI.create("/api/pesagens/" + salvo.getId())).body(Mapeador.toResponse(salvo));
    }

    @PutMapping("/{id}")
    public PesagemResponse atualizar(@PathVariable("id") Long id, @RequestBody PesagemRequest request) {
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

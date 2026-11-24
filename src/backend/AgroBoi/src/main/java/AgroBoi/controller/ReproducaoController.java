package AgroBoi.controller;

import AgroBoi.domain.dto.request.ReproducaoRequest;
import AgroBoi.domain.dto.response.ReproducaoResponse;
import AgroBoi.domain.entity.Reproducao;
import AgroBoi.mapper.Mapeador;
import AgroBoi.service.ReproducaoService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/reproducoes")
public class ReproducaoController {
    private final ReproducaoService service;

    public ReproducaoController(ReproducaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<ReproducaoResponse> listar(@RequestParam(name = "matrizId", required = false) Long matrizId) {
        return service.listarTodos().stream()
                .map(Mapeador::toResponse)
                .filter(r -> matrizId == null || matrizId.equals(r.matrizId()))
                .toList();
    }

    @GetMapping("/{id}")
    public ReproducaoResponse buscar(@PathVariable("id") Long id) {
        return service.buscarPorId(id).map(Mapeador::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro de reprodução não encontrado."));
    }

    @PostMapping
    public ResponseEntity<ReproducaoResponse> criar(@RequestBody ReproducaoRequest request) {
        Reproducao salvo = service.salvar(Mapeador.toEntity(request, null));
        return ResponseEntity.created(URI.create("/api/reproducoes/" + salvo.getId())).body(Mapeador.toResponse(salvo));
    }

    @PutMapping("/{id}")
    public ReproducaoResponse atualizar(@PathVariable("id") Long id, @RequestBody ReproducaoRequest request) {
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

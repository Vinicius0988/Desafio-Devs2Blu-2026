package AgroBoi.controller;

import AgroBoi.domain.dto.request.RegistroSanitarioRequest;
import AgroBoi.domain.dto.response.RegistroSanitarioResponse;
import AgroBoi.domain.entity.RegistroSanitario;
import AgroBoi.domain.enums.TipoSanitario;
import AgroBoi.mapper.Mapeador;
import AgroBoi.service.RegistroSanitarioService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/registros-sanitarios")
public class RegistroSanitarioController {
    private final RegistroSanitarioService service;

    public RegistroSanitarioController(RegistroSanitarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<RegistroSanitarioResponse> listar(@RequestParam(name = "animalId", required = false) Long animalId,
            @RequestParam(name = "tipo", required = false) TipoSanitario tipo) {
        return service.listarTodos().stream()
                .map(Mapeador::toResponse)
                .filter(r -> animalId == null || animalId.equals(r.animalId()))
                .filter(r -> tipo == null || tipo == r.tipo())
                .toList();
    }

    @GetMapping("/{id}")
    public RegistroSanitarioResponse buscar(@PathVariable("id") Long id) {
        return service.buscarPorId(id).map(Mapeador::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Registro sanitário não encontrado."));
    }

    @PostMapping
    public ResponseEntity<RegistroSanitarioResponse> criar(@RequestBody RegistroSanitarioRequest request) {
        RegistroSanitario salvo = service.salvar(Mapeador.toEntity(request, null));
        return ResponseEntity.created(URI.create("/api/registros-sanitarios/" + salvo.getId())).body(Mapeador.toResponse(salvo));
    }

    @PutMapping("/{id}")
    public RegistroSanitarioResponse atualizar(@PathVariable("id") Long id, @RequestBody RegistroSanitarioRequest request) {
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

package AgroBoi.controller;

import AgroBoi.domain.dto.request.MovimentacaoRequest;
import AgroBoi.domain.dto.response.MovimentacaoResponse;
import AgroBoi.domain.entity.Movimentacao;
import AgroBoi.mapper.Mapeador;
import AgroBoi.service.MovimentacaoService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Entradas e saídas de animais. Movimentações confirmadas não são editadas nem excluídas; saída por venda usa /api/vendas. */
@RestController
@RequestMapping("/api/movimentacoes")
public class MovimentacaoController {
    private final MovimentacaoService service;

    public MovimentacaoController(MovimentacaoService service) {
        this.service = service;
    }

    @GetMapping
    public List<MovimentacaoResponse> listar(@RequestParam(name = "animalId", required = false) Long animalId) {
        return service.listarTodos().stream()
                .map(Mapeador::toResponse)
                .filter(m -> animalId == null || animalId.equals(m.animalId()))
                .toList();
    }

    @GetMapping("/{id}")
    public MovimentacaoResponse buscar(@PathVariable("id") Long id) {
        return service.buscarPorId(id).map(Mapeador::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Movimentação não encontrada."));
    }

    @PostMapping
    public ResponseEntity<MovimentacaoResponse> criar(@RequestBody MovimentacaoRequest request) {
        Movimentacao salva = service.salvar(Mapeador.toEntity(request));
        return ResponseEntity.created(URI.create("/api/movimentacoes/" + salva.getId())).body(Mapeador.toResponse(salva));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        buscar(id);
        service.excluir(id); // lança RegraNegocioException: movimentações são preservadas
        return ResponseEntity.noContent().build();
    }
}

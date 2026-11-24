package AgroBoi.controller;

import AgroBoi.domain.dto.request.AnimalRequest;
import AgroBoi.domain.dto.response.AnimalResponse;
import AgroBoi.domain.entity.Animal;
import AgroBoi.domain.enums.Sexo;
import AgroBoi.domain.enums.StatusAnimal;
import AgroBoi.mapper.Mapeador;
import AgroBoi.service.AnimalService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * RF03 (cadastro), RF04 (consulta), RF05 (edição/exclusão) e RF15 (pesquisa).
 * GET /api/animais?busca=texto&racaId=1&situacao=NA_FAZENDA&sexo=FEMEA (todos os filtros são opcionais).
 */
@RestController
@RequestMapping("/api/animais")
public class AnimalController {
    private final AnimalService service;

    public AnimalController(AnimalService service) {
        this.service = service;
    }

    @GetMapping
    public List<AnimalResponse> listar(
            @RequestParam(name = "busca", required = false) String busca,
            @RequestParam(name = "racaId", required = false) Long racaId,
            @RequestParam(name = "situacao", required = false) StatusAnimal situacao,
            @RequestParam(name = "sexo", required = false) Sexo sexo) {
        return service.pesquisar(busca).stream()
                .map(Mapeador::toResponse)
                .filter(a -> racaId == null || racaId.equals(a.racaId()))
                .filter(a -> situacao == null || situacao == a.situacao())
                .filter(a -> sexo == null || sexo == a.sexo())
                .toList();
    }

    @GetMapping("/{id}")
    public AnimalResponse buscar(@PathVariable("id") Long id) {
        return service.buscarPorId(id).map(Mapeador::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Animal não encontrado."));
    }

    @PostMapping
    public ResponseEntity<AnimalResponse> criar(@RequestBody AnimalRequest request) {
        Animal salvo = service.salvar(Mapeador.toEntity(request, null));
        return ResponseEntity.created(URI.create("/api/animais/" + salvo.getId())).body(Mapeador.toResponse(salvo));
    }

    @PutMapping("/{id}")
    public AnimalResponse atualizar(@PathVariable("id") Long id, @RequestBody AnimalRequest request) {
        Animal atual = service.buscarPorId(id)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Animal não encontrado."));
        Animal novo = Mapeador.toEntity(request, id);
        // A situação só muda por movimentação/venda; sem situação no corpo, preserva a atual.
        if (request.situacao() == null) novo.setSituacao(atual.getSituacao());
        return Mapeador.toResponse(service.salvar(novo));
    }

    /** O AnimalService bloqueia a exclusão para preservar o histórico: responde 400 com a orientação. */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        buscar(id);
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

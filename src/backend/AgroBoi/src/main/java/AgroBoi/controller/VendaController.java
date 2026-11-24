package AgroBoi.controller;

import AgroBoi.domain.dto.request.VendaRequest;
import AgroBoi.domain.dto.response.VendaResponse;
import AgroBoi.domain.entity.Venda;
import AgroBoi.mapper.Mapeador;
import AgroBoi.service.ItemVendaService;
import AgroBoi.service.VendaService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** RF12: a venda é gravada com todos os itens (animais e valores) na mesma transação. */
@RestController
@RequestMapping("/api/vendas")
public class VendaController {
    private final VendaService service;
    private final ItemVendaService itens;

    public VendaController(VendaService service, ItemVendaService itens) {
        this.service = service;
        this.itens = itens;
    }

    private VendaResponse resposta(Venda venda) {
        return Mapeador.toResponse(venda, itens.listarPorVenda(venda.getId()));
    }

    @GetMapping
    public List<VendaResponse> listar() {
        return service.listarTodos().stream().map(this::resposta).toList();
    }

    @GetMapping("/{id}")
    public VendaResponse buscar(@PathVariable("id") Long id) {
        return service.buscarPorId(id).map(this::resposta)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Venda não encontrada."));
    }

    @PostMapping
    public ResponseEntity<VendaResponse> criar(@RequestBody VendaRequest request) {
        Venda salva = service.salvar(request);
        return ResponseEntity.created(URI.create("/api/vendas/" + salva.getId())).body(resposta(salva));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable("id") Long id) {
        buscar(id);
        service.excluir(id); // lança RegraNegocioException: vendas confirmadas não são excluídas
        return ResponseEntity.noContent().build();
    }
}

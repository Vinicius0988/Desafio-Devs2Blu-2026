package AgroBoi.controller;

import AgroBoi.domain.dto.response.ItemVendaResponse;
import AgroBoi.mapper.Mapeador;
import AgroBoi.service.ItemVendaService;
import java.util.List;
import org.springframework.web.bind.annotation.*;

/** Somente consulta: os itens são criados junto da venda (POST /api/vendas). */
@RestController
@RequestMapping("/api/itens-venda")
public class ItemVendaController {
    private final ItemVendaService service;

    public ItemVendaController(ItemVendaService service) {
        this.service = service;
    }

    @GetMapping
    public List<ItemVendaResponse> listar(@RequestParam(name = "vendaId", required = false) Long vendaId) {
        var lista = vendaId == null ? service.listarTodos() : service.listarPorVenda(vendaId);
        return lista.stream().map(Mapeador::toResponse).toList();
    }

    @GetMapping("/{id}")
    public ItemVendaResponse buscar(@PathVariable("id") Long id) {
        return service.buscarPorId(id).map(Mapeador::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Item de venda não encontrado."));
    }
}

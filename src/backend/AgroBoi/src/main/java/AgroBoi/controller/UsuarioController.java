package AgroBoi.controller;

import AgroBoi.domain.dto.request.UsuarioRequest;
import AgroBoi.domain.dto.response.UsuarioResponse;
import AgroBoi.mapper.Mapeador;
import AgroBoi.service.UsuarioService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Gestão de usuários: somente ADMINISTRADOR (regra definida no SecurityConfig). */
@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {
    private final UsuarioService service;

    public UsuarioController(UsuarioService service) {
        this.service = service;
    }

    @GetMapping
    public List<UsuarioResponse> listar() {
        return service.listarTodos().stream().map(Mapeador::toResponse).toList();
    }

    @GetMapping("/{id}")
    public UsuarioResponse buscar(@PathVariable("id") Long id) {
        return service.buscarPorId(id).map(Mapeador::toResponse)
                .orElseThrow(() -> new RecursoNaoEncontradoException("Usuário não encontrado."));
    }

    /** Senha nula mantém a senha atual. */
    @PutMapping("/{id}")
    public UsuarioResponse atualizar(@PathVariable("id") Long id, @RequestBody UsuarioRequest request) {
        buscar(id);
        return Mapeador.toResponse(service.atualizar(id, request));
    }

    /** Desativa o usuário (não apaga o cadastro). */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desativar(@PathVariable("id") Long id) {
        buscar(id);
        service.excluir(id);
        return ResponseEntity.noContent().build();
    }
}

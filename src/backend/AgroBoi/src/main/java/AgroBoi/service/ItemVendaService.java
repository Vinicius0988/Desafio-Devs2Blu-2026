package AgroBoi.service;

import AgroBoi.domain.entity.Itemvenda;
import AgroBoi.repository.ItemVendaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ItemVendaService {
    private final ItemVendaRepository repository;

    public ItemVendaService(ItemVendaRepository repository) {
        this.repository = repository;
    }

    public List<Itemvenda> listarTodos() {
        return repository.findAll();
    }

    public Optional<Itemvenda> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Itemvenda salvar(Itemvenda registro) {
        return repository.save(registro);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}


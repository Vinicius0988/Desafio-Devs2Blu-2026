package AgroBoi.service;

import AgroBoi.domain.entity.Animal;
import AgroBoi.repository.AnimalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AnimalService {
    private final AnimalRepository repository;

    public AnimalService(AnimalRepository repository) {
        this.repository = repository;
    }

    public List<Animal> listarTodos() {
        return repository.findAll();
    }

    public Optional<Animal> buscarPorId(Long id) {
        return repository.findById(id);
    }

    public Animal salvar(Animal registro) {
        return repository.save(registro);
    }

    public void excluir(Long id) {
        repository.deleteById(id);
    }
}


package AgroBoi.service;

import AgroBoi.domain.entity.Animal;
import AgroBoi.domain.enums.StatusAnimal;
import AgroBoi.domain.enums.TipoMovimentacao;
import AgroBoi.repository.AnimalRepository;
import AgroBoi.repository.MovimentacaoRepository;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

@Component
public class AnimalReferencia {
    private final AnimalRepository animais;
    private final MovimentacaoRepository movimentos;
    public AnimalReferencia(AnimalRepository animais, MovimentacaoRepository movimentos) {
        this.animais = animais; this.movimentos = movimentos;
    }
    public Animal buscar(Animal referencia, boolean obrigatorio) {
        if (referencia == null && !obrigatorio) return null;
        Regras.obrigatorio(referencia, "Animal");
        return buscarId(Regras.obrigatorio(referencia.getId(), "ID do animal"));
    }
    public Animal buscarId(Long id) {
        Regras.exigir(id != null && id > 0, "ID do animal inválido.");
        return animais.buscarComBloqueio(id).orElseThrow(() -> new RegraNegocioException("Animal não encontrado."));
    }
    public void validarData(Animal animal, LocalDate data) {
        Regras.data(data, "Data do evento");
        if (animal == null) return;
        Regras.exigir(animal.getDataNascimento() == null || !data.isBefore(animal.getDataNascimento()),
            "Evento anterior ao nascimento do animal.");
        var historico = movimentos.findByAnimalIdOrderByDataAscIdAsc(animal.getId());
        boolean presente = historico.isEmpty() ? animal.getSituacao() == StatusAnimal.NA_FAZENDA
            : historico.getFirst().getTipo() == TipoMovimentacao.SAIDA;
        for (var movimento : historico) {
            if (movimento.getData().isAfter(data)) break;
            // Registros do dia da saída são permitidos: o modelo não guarda horário.
            if (movimento.getData().equals(data) && movimento.getTipo() == TipoMovimentacao.SAIDA) continue;
            presente = movimento.getTipo() == TipoMovimentacao.ENTRADA;
        }
        Regras.exigir(presente, "O animal não estava na fazenda na data informada.");
    }
}

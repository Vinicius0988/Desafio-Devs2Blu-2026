package AgroBoi.service;

import AgroBoi.repository.*;
import java.time.LocalDate;
import org.springframework.stereotype.Component;

/** Impede que uma movimentação invalide registros de manejo já existentes. */
@Component
public class HistoricoAnimal {
    private final AlimentacaoRepository alimentacoes;
    private final PesagemRepository pesagens;
    private final RegistroSanitarioRepository sanitarios;
    private final ReproducaoRepository reproducoes;

    public HistoricoAnimal(AlimentacaoRepository alimentacoes, PesagemRepository pesagens,
            RegistroSanitarioRepository sanitarios, ReproducaoRepository reproducoes) {
        this.alimentacoes = alimentacoes;
        this.pesagens = pesagens;
        this.sanitarios = sanitarios;
        this.reproducoes = reproducoes;
    }

    public void validarSaida(Long id, LocalDate data) {
        boolean posterior = alimentacoes.existsByAnimalIdAndDataAfter(id, data)
            || pesagens.existsByAnimalIdAndDataHoraAfter(id, data.atTime(java.time.LocalTime.MAX))
            || sanitarios.existsByAnimalIdAndDataAplicacaoAfter(id, data)
            || reproducoes.existeEventoDepois(id, data);
        Regras.exigir(!posterior, "Há registros de manejo posteriores à saída informada.");
    }

    public void validarEntradaInicial(Long id, LocalDate data) {
        boolean anterior = alimentacoes.existsByAnimalIdAndDataBefore(id, data)
            || pesagens.existsByAnimalIdAndDataHoraBefore(id, data.atStartOfDay())
            || sanitarios.existsByAnimalIdAndDataAplicacaoBefore(id, data)
            || reproducoes.existeEventoAntes(id, data);
        Regras.exigir(!anterior, "Há registros de manejo anteriores à entrada informada.");
    }
}

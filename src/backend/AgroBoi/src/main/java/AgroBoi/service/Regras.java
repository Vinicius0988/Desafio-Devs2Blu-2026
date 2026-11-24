package AgroBoi.service;

import java.math.BigDecimal;
import java.time.LocalDate;

final class Regras {
    private Regras() {}
    static void exigir(boolean condicao, String mensagem) {
        if (!condicao) throw new RegraNegocioException(mensagem);
    }
    static <T> T obrigatorio(T valor, String campo) {
        exigir(valor != null, campo + " é obrigatório."); return valor;
    }
    static String texto(String valor, int tamanho, String campo) {
        exigir(valor != null && !valor.isBlank(), campo + " é obrigatório.");
        String normalizado = valor.trim();
        exigir(normalizado.length() <= tamanho, campo + " excede " + tamanho + " caracteres.");
        return normalizado;
    }
    static String opcional(String valor, int tamanho, String campo) {
        return valor == null || valor.isBlank() ? null : texto(valor, tamanho, campo);
    }
    static void decimal(BigDecimal valor, int inteiros, int casas, String campo) {
        obrigatorio(valor, campo); exigir(valor.signum() > 0, campo + " deve ser positivo.");
        BigDecimal limpo = valor.stripTrailingZeros();
        exigir(limpo.scale() <= casas && limpo.precision() - limpo.scale() <= inteiros,
            campo + " possui precisão inválida.");
    }
    static void data(LocalDate data, String campo) {
        obrigatorio(data, campo); exigir(!data.isAfter(LocalDate.now()), campo + " não pode estar no futuro.");
    }
}

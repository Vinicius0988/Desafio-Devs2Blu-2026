const AnimalContract = {
  labels: {MACHO: "Macho", FEMEA: "Fêmea", NA_FAZENDA: "Na fazenda", VENDIDO: "Vendido", MORTO: "Morto", SAIDA_OUTRA: "Outra saída"},
  request(data) {
    const id = value => {
      if (value == null || value === "") return null;
      const number = Number(value);
      if (!Number.isSafeInteger(number) || number <= 0) throw new Error("Informe IDs inteiros positivos.");
      return number;
    };
    const identificacao = (data.identificacao ?? "").trim();
    const nome = (data.nome ?? "").trim();
    if (!identificacao || identificacao.length > 50 || nome.length > 100) throw new Error("Confira a identificação (até 50 caracteres) e o nome (até 100).");
    if (!["MACHO", "FEMEA"].includes(data.sexo)) throw new Error("Selecione o sexo.");
    if (!["NA_FAZENDA", "VENDIDO", "MORTO", "SAIDA_OUTRA"].includes(data.situacao)) throw new Error("Selecione uma situação válida.");
    return {identificacao, nome: nome || null, sexo: data.sexo, dataNascimento: data.dataNascimento || null,
      racaId: id(data.racaId), maeId: id(data.maeId), paiId: id(data.paiId), situacao: data.situacao,
      observacoes: (data.observacoes ?? "").trim() || null};
  }
};
if (typeof module !== "undefined") module.exports = AnimalContract;

# Regras de negócio dos services

A aplicação gerencia uma única fazenda. Os services validam os dados antes de acessar os repositories e usam transações nas gravações. Violações das regras lançam `RegraNegocioException`.

## Regras por serviço

| Serviço | Responsabilidade e regras |
|---|---|
| Fazenda | Salva sempre o cadastro de ID 1. Exige nome e responsável, normaliza UF e impede a exclusão da fazenda. |
| Usuario | Exige nome, email válido e senha no cadastro. Normaliza email e verifica duplicidade. Guarda a senha com BCrypt; na atualização, senha nula preserva o hash. A exclusão desativa o usuário, sem apagar o cadastro. Administradores não são desativados por esse fluxo. |
| Raca | Exige nome e verifica duplicidade sem diferenciar maiúsculas e minúsculas. Atualizações exigem um ID existente. O banco mantém a integridade das referências ao excluir. |
| Animal | Exige identificação única e sexo. Normaliza a identificação em maiúsculas. Valida raça, pai e mãe existentes, sexo dos genitores, nascimento anterior ao filho quando conhecido e ausência de ciclos na genealogia. |
| Alimentacao | Aceita registro geral ou vinculado a um animal. Exige data e descrição. Quantidade e unidade devem ser informadas juntas; quantidade deve ser positiva. |
| Pesagem | Exige animal existente, data/hora não futura e peso positivo, com até três casas decimais. |
| RegistroSanitario | Exige animal, tipo, data de aplicação e descrição. Dose e unidade devem ser informadas juntas. A próxima aplicação não pode anteceder a aplicação registrada. |
| Reproducao | Exige matriz fêmea. Reprodutor cadastrado deve ser macho; não pode ser informado junto de uma referência externa. Previsão de parto não pode anteceder o evento. Parto e perda gestacional exigem data de desfecho, não futura e igual ou posterior ao evento. |
| Despesa | Exige categoria, descrição, data não futura e valor positivo, com até duas casas decimais. Animal é opcional; despesas podem ser registradas depois de sua saída. |
| Venda | Exige data, comprador e pelo menos um item. Rejeita animais repetidos, inexistentes ou indisponíveis e valores inválidos. Grava venda, itens, saídas e situação VENDIDO na mesma transação. |
| ItemVenda | Permite consultas. Inclusão acontece exclusivamente pelo serviço de vendas, para não deixar itens sem a saída correspondente. |
| Movimentacao | Confere tipo, motivo, situação atual e ordem das datas. Entrada coloca o animal NA_FAZENDA; morte coloca MORTO; outra saída coloca SAIDA_OUTRA. Venda usa o serviço de vendas. Animal morto não pode retornar. |

## Datas, histórico e integridade

- Eventos realizados não podem ter datas futuras. Previsão de parto e próxima aplicação podem ser futuras.
- Alimentação individual, pesagem, atendimento e reprodução devem ocorrer durante a permanência do animal na fazenda e depois de seu nascimento, quando conhecido. O desfecho reprodutivo também verifica a presença da matriz.
- Eventos no dia da saída são aceitos, pois a movimentação não possui horário.
- Novas movimentações não podem anteceder a última movimentação. Uma saída não pode anteceder registros de manejo já salvos. A primeira entrada não pode ser posterior a registros de manejo existentes.
- O cadastro inicial considera o animal presente. A primeira movimentação de entrada pode completar esse histórico; nascimento exige a mesma data do cadastro.
- Quantidades, doses, pesos e valores devem ser positivos e caber na precisão definida no banco, sem arredondamento silencioso.
- Animais envolvidos nas gravações são carregados com bloqueio de escrita. Nas vendas, os bloqueios seguem a ordem dos IDs. Isso impede que duas vendas confirmem simultaneamente a disponibilidade do mesmo animal.
- Restrições de unicidade e chaves estrangeiras do banco continuam sendo a proteção final, inclusive em concorrência.

## Decisões desta implementação

- O cadastro comum não altera sexo ou nascimento de animais existentes. Corrigir esses dados exige revalidar descendentes e histórico, em um fluxo específico futuro.
- A situação do animal muda por venda ou movimentação. A exclusão de animais, vendas, itens e movimentações confirmadas fica bloqueada para preservar o histórico. Estorno e correção histórica ainda não estão implementados.
- Reprodução não cria automaticamente o bezerro nem presume uma duração fixa de gestação.
- Usuários novos recebem USUARIO_COMUM. O login JWT e o provisionamento do administrador por variáveis de ambiente estão implementados. O cadastro é público e apenas a gestão de usuários exige administrador; restringir as demais operações ao fazendeiro ainda é uma pendência.

## Uso pela API REST

`VendaService.salvar(VendaRequest)` recebe a negociação e todos os itens juntos. `UsuarioService.salvar(UsuarioRequest)` cria o usuário e `atualizar(id, UsuarioRequest)` altera seu cadastro. Os demais métodos de gravação recebem as entidades com referências por ID, que são consultadas e validadas novamente.

Os controllers usam `Mapeador` para converter DTOs de entrada em novos objetos e devolver DTOs de resposta. O hash da senha não é exposto. `GlobalExceptionHandler` converte as exceções previstas em respostas HTTP; a integração com o frontend permanece pendente.

Consulte o [README principal](../README.md) para endpoints, autenticação, configuração e formato dos erros.

## Autenticação, dashboard e relatórios

- `AuthService` verifica email, senha BCrypt e conta ativa. O filtro JWT também consulta a conta, bloqueando usuários desativados.
- `AdminInicial` cria a conta administrativa quando as variáveis estão definidas e o email ainda não existe. Não transforma uma conta comum existente em administrador.
- `DashboardService` calcula totais por situação e sexo, previsões sanitárias, pesagens, nascimentos, receitas e despesas do mês, além das dez últimas movimentações.
- `RelatorioService` fornece rebanho, vacinação, evolução de peso, reprodução e financeiro. Datas finais anteriores às iniciais são rejeitadas. O financeiro soma os valores dos itens vendidos e subtrai despesas; não representa controle de parcelas ou recebimentos.
- Alertas sanitários são contagens baseadas em `proxima_aplicacao`; não existe fluxo de baixa de lembrete nem exclusão automática de alertas de animais que saíram da fazenda.

## Testes

Na pasta `src/backend/AgroBoi`, executar com JDK 21 ou superior:

```powershell
./mvnw.cmd -B -ntp test
```

A suíte contém testes unitários de validação e testes de integração com H2 em modo MariaDB, usando apenas o perfil `test`. Verifica também a inicialização do Spring, as consultas dos repositories, o rollback de uma venda que falha no segundo item e o fluxo REST com autenticação. Os detalhes e comandos estão no [README principal](../README.md#testes).

H2 é uma dependência exclusiva de testes. O banco real da fazenda não é alterado por essa suíte. Os testes não substituem a validação posterior contra uma instância real de MariaDB/MySQL, especialmente quanto a concorrência, collation e diferenças de SQL.

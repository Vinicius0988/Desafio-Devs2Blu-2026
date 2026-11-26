# DTOs do domínio

`request` contém os dados recebidos pela API REST; `response`, os dados devolvidos ao cliente. São records Java, sem anotações JPA. O frontend atual é HTML/CSS/JavaScript e ainda não consome esses contratos.

- IDs e datas de criação são retornados, não recebidos para cadastro. Em atualizações, o ID vem pela rota.
- Relacionamentos usam IDs, incluindo raça, mãe e pai. Os services consultam e validam as referências.
- `VendaRequest` recebe uma lista de `ItemVendaRequest` com `animalId` e `valor`. O serviço grava venda, itens, saídas e situação dos animais na mesma transação. `VendaResponse` inclui os itens.
- `UsuarioRequest` recebe nome, email e senha. `UsuarioService` gera o hash BCrypt e atribui `USUARIO_COMUM` ao cadastro. Na atualização, senha nula preserva a atual. `UsuarioResponse` não retorna senha nem hash.
- `LoginRequest` recebe email e senha; `LoginResponse` devolve token, tipo, validade em segundos e dados do usuário. O `toString` de `LoginRequest` e `UsuarioRequest` omite credenciais.
- `ErroResposta` representa os erros tratados pela aplicação; respostas do filtro de segurança podem não usar esse formato.
- `DashboardResponse` e os records agrupados em `Relatorios` representam os indicadores e relatórios calculados pelos services.
- Campos opcionais podem ser nulos. Obrigatoriedade, tamanho, valores e coerência são validados nos services; os records não usam Bean Validation.

## Conversão e valores padrão

Os controllers usam [Mapeador](../../mapper/Mapeador.java) para construir novas entidades e produzir respostas. As entidades não são serializadas diretamente. O mapeador lê IDs de referências lazy sem inicializar os objetos relacionados.

Sem `situacao` no cadastro do animal, permanece `NA_FAZENDA`; na atualização, o controller preserva a situação existente. Ela só muda por venda ou movimentação. Sem `resultado` no pedido de reprodução, o mapeador usa `PENDENTE`, inclusive na atualização; envie o resultado que deseja manter.

`MovimentacaoRequest.itemVendaId` é ignorado pelo mapeador. Movimentações de venda são criadas exclusivamente por `VendaService`; não é possível vincular um item de venda pelo endpoint comum de movimentações.

Os pacotes seguem as pastas `AgroBoi.domain.dto.request` e `AgroBoi.domain.dto.response`. Endpoints e configuração estão no [README principal](../../../../../../../../../README.md).

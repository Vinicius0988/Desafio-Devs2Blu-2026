# AgroBoi — Explicação das tabelas e campos

Este documento explica a estrutura do arquivo [agroboi_mariadb.sql](agroboi_mariadb.sql). O sistema atende uma única fazenda, gerenciada pelo próprio fazendeiro.

## 1. fazenda

Guarda os dados da única propriedade atendida pelo sistema. Cada registro representa uma fazenda, mas o banco permite apenas o identificador `1`.

| Campo | O que representa |
|---|---|
| `id` | Identificador fixo da fazenda: `1`. |
| `nome` | Nome da propriedade. |
| `nome_responsavel` | Nome do fazendeiro responsável. |
| `municipio` | Município onde a fazenda está localizada. |
| `uf` | Sigla do estado, como SC ou PR. |
| `endereco` | Endereço ou localização da propriedade. |

A regra `CHECK (id = 1)`, junto da chave primária, impede o cadastro de uma segunda fazenda.

## 2. usuario

Guarda as contas que acessam o sistema. O usuário principal é o próprio fazendeiro.

| Campo | O que representa |
|---|---|
| `id` | Identificador da conta. |
| `nome` | Nome do usuário. |
| `email` | Email utilizado no login; não pode se repetir. |
| `senha_hash` | Hash da senha, sem armazenar a senha original. |
| `perfil` | Nível de acesso, como ADMINISTRADOR ou USUARIO_COMUM; o padrão é ADMINISTRADOR. |
| `ativo` | Indica se a conta está habilitada; começa como verdadeira. |
| `criado_em` | Data e horário em que a conta foi cadastrada. |

O perfil de usuário comum foi previsto para atender aos critérios acadêmicos do projeto.

## 3. raca

Guarda as raças disponíveis para classificar os animais. Cada registro representa uma raça.

| Campo | O que representa |
|---|---|
| `id` | Identificador da raça. |
| `nome` | Nome da raça, como Nelore ou Angus; não pode se repetir. |

## 4. animal

É o cadastro principal do rebanho. Cada registro representa um animal, inclusive depois de sua venda ou saída da fazenda.

| Campo | O que representa |
|---|---|
| `id` | Identificador interno do animal. |
| `identificacao` | Brinco ou código usado pelo fazendeiro; não pode se repetir. |
| `nome` | Nome do animal, caso tenha. |
| `sexo` | Sexo do animal: MACHO ou FEMEA. |
| `data_nascimento` | Data de nascimento, quando conhecida. |
| `raca_id` | Referência à raça cadastrada em `raca`. |
| `mae_id` | Referência ao `id` da mãe, cadastrada na própria tabela `animal`. |
| `pai_id` | Referência ao `id` do pai, cadastrado na própria tabela `animal`. |
| `situacao` | Estado atual: NA_FAZENDA, VENDIDO, MORTO ou SAIDA_OUTRA; começa como NA_FAZENDA. |
| `observacoes` | Informações adicionais sobre o animal. |
| `criado_em` | Data e horário do cadastro. |

Mãe, pai e raça podem ficar sem informação. Por exemplo, `mae_id = 5` significa que a mãe é o animal cujo `id` é `5`.

## 5. pesagem

Guarda o histórico de peso dos animais. Cada registro representa uma pesagem; um animal pode ter várias.

| Campo | O que representa |
|---|---|
| `id` | Identificador da pesagem. |
| `animal_id` | Animal que foi pesado. |
| `data_hora` | Data e horário da pesagem. |
| `peso_kg` | Peso do animal em quilogramas. |
| `observacoes` | Informações adicionais sobre a pesagem. |

## 6. registro_sanitario

Guarda o histórico de vacinação, medicamentos e outros procedimentos de saúde. Cada registro representa uma aplicação ou procedimento em um animal.

| Campo | O que representa |
|---|---|
| `id` | Identificador do registro de saúde. |
| `animal_id` | Animal atendido. |
| `tipo` | Tipo de atendimento: VACINA, MEDICAMENTO ou PROCEDIMENTO. |
| `data_aplicacao` | Data em que a aplicação ou procedimento foi realizado. |
| `descricao` | Nome do produto ou descrição do procedimento. |
| `dose` | Quantidade aplicada, quando houver. |
| `unidade_dose` | Unidade da dose, como mL ou mg. |
| `proxima_aplicacao` | Data prevista para repetir a aplicação, se necessário. |
| `observacoes` | Informações adicionais sobre o atendimento. |

## 7. reproducao

Guarda os eventos reprodutivos e seu acompanhamento. Cada registro representa uma monta ou inseminação e seu resultado.

| Campo | O que representa |
|---|---|
| `id` | Identificador do registro de reprodução. |
| `matriz_id` | Referência à fêmea cadastrada em `animal`. |
| `reprodutor_id` | Referência ao macho cadastrado em `animal`, se conhecido. |
| `referencia_reprodutor_externo` | Identificação de touro ou sêmen externo, quando não houver reprodutor cadastrado. |
| `tipo` | Método utilizado: MONTA_NATURAL ou INSEMINACAO. |
| `data_evento` | Data da monta ou inseminação. |
| `resultado` | Situação do acompanhamento: PENDENTE, GESTANTE, NAO_GESTANTE, PARTO ou PERDA_GESTACIONAL; começa como PENDENTE. |
| `data_prevista_parto` | Previsão de nascimento do bezerro. |
| `data_desfecho` | Data em que ocorreu o parto ou a perda gestacional. |
| `observacoes` | Informações adicionais sobre a reprodução. |

O bezerro nascido é cadastrado separadamente em `animal`, com os vínculos de mãe e pai conhecidos.

## 8. alimentacao

Guarda os registros de alimentação. Cada registro representa uma alimentação ou manejo alimentar de um animal ou do rebanho inteiro.

| Campo | O que representa |
|---|---|
| `id` | Identificador do registro de alimentação. |
| `animal_id` | Animal alimentado; sem valor, o registro se refere ao rebanho inteiro. |
| `data` | Data da alimentação. |
| `descricao` | Alimento fornecido ou descrição do manejo. |
| `quantidade` | Quantidade total fornecida, quando informada. |
| `unidade` | Unidade da quantidade, como kg ou litros. |
| `observacoes` | Informações adicionais sobre a alimentação. |

## 9. despesa

Guarda os gastos da propriedade. Cada registro representa uma despesa associada a um animal ou à fazenda em geral.

| Campo | O que representa |
|---|---|
| `id` | Identificador da despesa. |
| `animal_id` | Animal relacionado ao gasto; sem valor, a despesa é geral. |
| `categoria` | Classificação do gasto: ALIMENTACAO, SAUDE, COMPRA_ANIMAL, MANUTENCAO ou OUTRA. |
| `descricao` | Explicação do que foi comprado ou pago. |
| `data` | Data da despesa. |
| `valor` | Valor gasto em reais. |
| `observacoes` | Informações adicionais sobre a despesa. |

## 10. venda

Guarda os dados gerais de uma negociação. Cada registro representa uma venda, que pode reunir um ou mais animais.

| Campo | O que representa |
|---|---|
| `id` | Identificador da venda. |
| `data` | Data da venda. |
| `comprador` | Nome de quem comprou os animais. |
| `observacoes` | Informações adicionais sobre a negociação. |

Os animais e seus valores ficam em `item_venda`. O total da venda é a soma dos valores desses itens.

## 11. item_venda

Liga os animais às vendas. Cada registro representa um animal vendido e seu valor dentro de uma negociação.

| Campo | O que representa |
|---|---|
| `id` | Identificador do item da venda. |
| `venda_id` | Venda à qual o item pertence. |
| `animal_id` | Animal vendido. |
| `valor` | Valor de venda desse animal em reais. |

A regra `UNIQUE (venda_id, animal_id)` impede repetir o mesmo animal na mesma venda. Uma venda de três animais terá três registros em `item_venda`.

## 12. movimentacao

Guarda o histórico de entradas e saídas do rebanho. Cada registro representa a entrada ou saída de um animal.

| Campo | O que representa |
|---|---|
| `id` | Identificador da movimentação. |
| `animal_id` | Animal que entrou ou saiu. |
| `tipo` | Sentido da movimentação: ENTRADA ou SAIDA. |
| `motivo` | Motivo: NASCIMENTO, COMPRA, VENDA, MORTE ou OUTRO. |
| `data` | Data da movimentação. |
| `item_venda_id` | Item da venda que originou a saída, quando o motivo for venda. |
| `origem_destino` | Local ou pessoa de origem ou destino do animal. |
| `observacoes` | Informações adicionais sobre a movimentação. |

`item_venda_id` é opcional e único: outras movimentações ficam sem essa referência, e um item vendido pode estar ligado a no máximo uma movimentação.

## Observação sobre as validações

Os tipos, situações e categorias descritos aqui são os valores previstos para a aplicação. Na versão simplificada, o SQL guarda esses campos como texto; o backend deve validar os valores permitidos, datas, quantidades e regras de negócio.

Por exemplo, ao vender um animal, o backend deve registrar a venda, o item e a saída, além de atualizar a situação do animal, tudo na mesma transação.

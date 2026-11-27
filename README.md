# AgroBoi

Backend Java 21 com Spring Boot; frontend HTML/CSS/JavaScript.

## Estrutura e documentação

O projeto atende uma única fazenda. O backend usa Java 21 e Spring Boot; o frontend atual usa HTML, CSS e JavaScript, sem Angular.

- `src/backend/AgroBoi`: API, regras de negócio, persistência e testes.
- `src/frontend`: páginas e scripts da interface, ainda sem integração com a API.
- [Passo a passo de execução no IntelliJ](docs/EXECUCAO_INTELLIJ.md).
- [Banco e configuração do esquema](banco/README.md).
- [Dicionário das 12 tabelas](banco/DICIONARIO_DE_DADOS.md).
- [Regras de negócio e limitações](banco/REGRAS_NEGOCIO.md).
- [DTOs de entrada e resposta](src/backend/AgroBoi/src/main/java/AgroBoi/domain/dto/README.md).

## Banco e execução

Requisitos: JDK 21 ou superior, `JAVA_HOME` apontando para o JDK e um servidor MySQL ou MariaDB acessível. O Maven Wrapper está incluído; não é necessário instalar Maven separadamente. A primeira execução precisa baixar as dependências. Não há configuração Docker Compose versionada neste repositório.

A combinação padrão é `mariadb,dev`. Use `mysql,dev` para desenvolvimento com MySQL e `mariadb,prod` ou `mysql,prod` para produção. Selecione um banco e um ambiente; não combine os dois bancos ou `dev` com `prod`. Perfis explícitos substituem os padrões.

| Variável | Comportamento |
|---|---|
| `SPRING_PROFILES_ACTIVE` | Padrão `mariadb,dev` quando não houver perfis explícitos |
| `DB_URL` | Opcional; endereço local `localhost:3306/agroboi`, com protocolo do banco selecionado |
| `DB_USER` / `DB_PASSWORD` | Credenciais obrigatórias, sem valores padrão versionados |
| `JWT_SECRET` | Obrigatório: segredo estável com pelo menos 32 bytes UTF-8 |
| `JWT_EXPIRACAO_MINUTOS` | Inteiro positivo; padrão `480` |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` | Provisionamento inicial do fazendeiro; não altera conta existente |
| `CORS_ORIGENS` | Origens explícitas separadas por vírgula; padrão local `http://localhost:5500,http://127.0.0.1:5500`; obrigatório em `prod` |
| `DDL_AUTO` | Em `dev`, padrão `update`; em `prod`, não é utilizado, pois o esquema é apenas validado |
| `PORT` | Porta HTTP; padrão `8080` |

O banco `agroboi` deve existir. Desenvolvimento pode criar/ajustar tabelas; produção exige esquema previamente preparado. A troca de perfil não migra dados entre MySQL e MariaDB. A compatibilidade do SQL manual com o banco real permanece pendente de validação.

**Antes de executar pelo IntelliJ**, configure o JDK, Maven Wrapper e as variáveis da aplicação em **Run > Edit Configurations > Environment variables**. Siga o [guia completo do IntelliJ](docs/EXECUCAO_INTELLIJ.md), que inclui primeiro acesso, geração do segredo JWT, configuração do banco e diagnóstico de erros.

No terminal, após configurar as mesmas variáveis no ambiente, execute em `src/backend/AgroBoi`:

```powershell
./mvnw.cmd spring-boot:run
```

No Linux/macOS, use `./mvnw spring-boot:run`. O projeto não carrega `.env` automaticamente. As configurações do IntelliJ e as variáveis de um terminal são independentes.

Com o backend iniciado, acesse [Swagger UI](http://localhost:8080/swagger-ui.html), faça login e informe o token em **Authorize**. Sirva o frontend por HTTP em uma origem permitida; abrir por `file://` não permite chamadas à API. As telas ainda precisam ser integradas ao backend.

## API REST

Todos os endpoints abaixo têm o prefixo `/api` e usam JSON. Somente o login é público entre os endpoints da API; os demais exigem `Authorization: Bearer <token>`. Swagger/OpenAPI também são públicos.

As operações da fazenda exigem `ADMINISTRADOR`, perfil do fazendeiro. `GET /api/auth/me` permite que qualquer conta autenticada consulte apenas seus próprios dados. Contas comuns existentes não acessam os dados da fazenda.

O cadastro pela API foi removido e `/api/auth/cadastro` permanece bloqueado, inclusive para administradores: sem autenticação retorna 401; com autenticação, 403. O primeiro acesso do fazendeiro depende de `ADMIN_EMAIL` e `ADMIN_PASSWORD` na inicialização. Caso o email já pertença a uma conta comum, ela não será promovida automaticamente; use um email administrativo ainda não cadastrado ou regularize o perfil por administração controlada do banco.

Contas não são apagadas nem promovidas nesta alteração. O perfil é verificado no banco a cada requisição: retirar o perfil administrativo bloqueia o acesso à fazenda mesmo com token anterior; desativar a conta invalida seu acesso autenticado.

Na tabela, CRUD significa listar, buscar por ID, criar, atualizar e excluir. Filtros de consulta se aplicam à listagem; atualização e exclusão usam `/{id}`. As exceções estão indicadas abaixo.

| Recurso | Endpoints | Requisitos |
|---|---|---|
| Auth | `POST /auth/login`, `GET /auth/me` | RF02 |
| Usuários (só ADMINISTRADOR) | `GET /usuarios`, `GET/PUT/DELETE /usuarios/{id}` (DELETE desativa) | |
| Fazenda | `GET /fazenda`, `PUT /fazenda` (fazenda única, ID 1) | |
| Raças | CRUD `/racas` | |
| Animais | Listar, buscar, criar e atualizar `/animais` (exclusão bloqueada); `GET /animais?busca=&racaId=&situacao=&sexo=` | RF03, RF04, RF05, RF15 |
| Registros sanitários | CRUD `/registros-sanitarios?animalId=&tipo=VACINA\|MEDICAMENTO\|PROCEDIMENTO` | RF06, RF07 |
| Alimentação | CRUD `/alimentacoes?animalId=` | RF08 |
| Pesagens | CRUD `/pesagens?animalId=` | RF09 |
| Reprodução | CRUD `/reproducoes?matrizId=` | RF10 |
| Despesas | CRUD `/despesas?animalId=` | RF11 |
| Vendas | `GET /vendas`, `GET /vendas/{id}`, `POST /vendas` (venda + itens + saídas) | RF11, RF12 |
| Itens de venda | `GET /itens-venda?vendaId=`, `GET /itens-venda/{id}` | |
| Movimentações | `GET /movimentacoes?animalId=`, `GET /movimentacoes/{id}`, `POST /movimentacoes` | |
| Dashboard | `GET /dashboard` | RF13 |
| Relatórios | `GET /relatorios/rebanho`, `/vacinacao`, `/pesagens`, `/reproducao`, `/financeiro` (período opcional nos quatro últimos) | RF14 |

Os erros tratados pelo `GlobalExceptionHandler` usam `{timestamp, status, mensagem}`: 400 para regra de negócio, JSON ou parâmetro inválido; 404 para recurso não encontrado pelo controller; 409 para duplicidade ou violação de integridade. Referências inexistentes rejeitadas pelos services podem retornar 400.

O login inválido retorna 401 nesse mesmo formato. Respostas 401/403 produzidas pelo filtro de segurança não possuem garantia desse corpo JSON; o frontend deve verificar também o status HTTP.

Nos relatórios de vacinação, pesagens e reprodução, `inicio` e `fim` são opcionais e inclusivos; sem ambos, o histórico inteiro é considerado. O financeiro assume o primeiro dia do mês para `inicio` ausente e a data atual para `fim` ausente. O relatório de rebanho representa a situação atual e não recebe período. As listas atuais não têm paginação.

Para registros existentes, `DELETE` de animais, movimentações e vendas responde 400 (histórico é preservado); IDs inexistentes retornam 404. Receitas do financeiro = valor das vendas.

## Estado da implementação

Backend: entidades, repositories, services com regras, controllers, mapeador de DTOs, tratamento de erros, segurança JWT, administrador inicial, dashboard, relatórios e documentação OpenAPI implementados.

Frontend: animais, saúde e alimentação usam dados locais (`localStorage`); outras telas têm comportamento demonstrativo ou incompleto. `api.js` está vazio e o login apenas valida campos. A integração com o backend está pendente.

Pendências conhecidas:

- Validar o esquema, os fluxos e a concorrência no MySQL/MariaDB real.
- Integrar as telas e testar o fluxo completo pelo navegador.
- Implementar estorno e correções históricas, se incluídos na entrega; atualmente são bloqueados.
- Definir backup e restauração. Não há rotina de backup implementada na aplicação nem configuração de contêiner incluída.

## Testes

Na pasta `src/backend/AgroBoi`:

```powershell
./mvnw.cmd -B -ntp test
```

Os testes Spring usam o perfil `test`, H2 em memória e criação automática do esquema. Credenciais de banco, JWT e provisionamento administrativo são isolados nos recursos de teste; não é preciso definir variáveis locais. Não sobrescreva diretamente as propriedades Spring de conexão, pois os testes limpam registros.

A execução desta etapa passou nos 61 testes. A suíte cobre regras de negócio, integração de vendas, fluxo REST, controle de acesso, perfis, JWT, CORS e inicialização do Spring. Inclui login, acesso protegido, cadastro de animal, pesagem, venda, relatórios e rollback. Isso não substitui testes no banco real ou a integração do navegador.

Para os testes de contrato do frontend, com Node.js instalado, a partir da raiz:

```powershell
node --test src/frontend/tests/contracts.test.cjs
```

Esses testes verificam contratos e comportamento local; não fazem chamadas à API.

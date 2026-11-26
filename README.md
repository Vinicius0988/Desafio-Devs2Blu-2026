# AgroBoi

Backend Java 21 com Spring Boot; frontend HTML/CSS/JavaScript.

## Estrutura e documentação

O projeto atende uma única fazenda. O backend usa Java 21 e Spring Boot; o frontend atual usa HTML, CSS e JavaScript, sem Angular.

- `src/backend/AgroBoi`: API, regras de negócio, persistência e testes.
- `src/frontend`: páginas e scripts da interface, ainda sem integração com a API.
- [Banco e configuração do esquema](banco/README.md).
- [Dicionário das 12 tabelas](banco/DICIONARIO_DE_DADOS.md).
- [Regras de negócio e limitações](banco/REGRAS_NEGOCIO.md).
- [DTOs de entrada e resposta](src/backend/AgroBoi/src/main/java/AgroBoi/domain/dto/README.md).

## Banco e execução

Requisitos: JDK 21 ou superior, `JAVA_HOME` apontando para o JDK e um servidor MySQL ou MariaDB acessível. O Maven Wrapper está incluído; não é necessário instalar Maven separadamente. A primeira execução precisa baixar as dependências. Não há configuração Docker Compose versionada neste repositório.

O perfil padrão é `mysql`. Para MariaDB, use `SPRING_PROFILES_ACTIVE=mariadb`. Os perfis selecionam uma conexão por execução; não conectam os dois bancos simultaneamente.

Por padrão, `DDL_AUTO=update` permite ao Hibernate criar/ajustar tabelas, mas o banco `agroboi` deve existir. Outra opção é executar o [script MariaDB](banco/agroboi_mariadb.sql) em um banco novo e usar `DDL_AUTO=validate`; a compatibilidade desse esquema com uma instância real ainda precisa ser validada.

| Configuração | Comportamento atual |
|---|---|
| `SPRING_PROFILES_ACTIVE` | `mysql` por padrão; `mariadb` seleciona o outro driver |
| `DB_URL` | Opcional; padrão `jdbc:mysql://localhost:3306/agroboi` ou `jdbc:mariadb://localhost:3306/agroboi`, conforme o perfil |
| `DB_USER` | Usuário do banco; padrão `root` |
| `SPRING_DATASOURCE_PASSWORD` | Sobrescreve a senha da conexão. Atualmente há uma senha fixa no arquivo de configuração; `DB_PASSWORD` não é lida pelo código atual |
| `DDL_AUTO` | Padrão `update`; `validate` apenas verifica o esquema |
| `PORT` | Porta HTTP; padrão `8080` |
| `JWT_SECRET` | Segredo com pelo menos 32 bytes em UTF-8. Sem ele, a chave é aleatória e os tokens deixam de valer após reiniciar |
| `JWT_EXPIRACAO_MINUTOS` | Validade do token; padrão `480` minutos |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` | Cria administrador na inicialização se ambas estiverem definidas e o email ainda não existir; não promove nem redefine a senha de conta existente |
| `CORS_ORIGENS` | Origens permitidas, separadas por vírgula; padrão `*` |

Exemplo no PowerShell, a partir da raiz do repositório, com o banco já criado:

```powershell
$env:SPRING_PROFILES_ACTIVE = 'mariadb'
$env:DB_USER = 'seu_usuario'
# Defina SPRING_DATASOURCE_PASSWORD no ambiente com a senha real da conexão.
Set-Location src/backend/AgroBoi
./mvnw.cmd spring-boot:run
```

No Linux/macOS, use `./mvnw spring-boot:run` na mesma pasta. Configure `ADMIN_EMAIL` e `ADMIN_PASSWORD` antes da primeira execução para provisionar o fazendeiro como administrador.

Com o backend iniciado, acesse [Swagger UI](http://localhost:8080/swagger-ui.html). Faça login em `POST /api/auth/login` com `email` e `senha`; use o token retornado no botão **Authorize**. Nas requisições HTTP, envie `Authorization: Bearer <token>`.

Para visualizar o frontend, abra `src/frontend/index.html` no navegador. As páginas ainda não autenticam nem salvam dados no backend.

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

- Retirar a senha fixa da configuração e definir as configurações do ambiente de execução, incluindo JWT e CORS.
- Validar o esquema, os fluxos e a concorrência no MySQL/MariaDB real.
- Integrar as telas e testar o fluxo completo pelo navegador.
- Implementar estorno e correções históricas, se incluídos na entrega; atualmente são bloqueados.
- Definir backup e restauração. Não há rotina de backup implementada na aplicação nem configuração de contêiner incluída.

## Testes

Na pasta `src/backend/AgroBoi`:

```powershell
./mvnw.cmd -B -ntp test
```

Os testes Spring usam o perfil `test`, H2 em memória no modo MariaDB e criação automática do esquema. Não exigem um banco externo nem `DB_PASSWORD`. Execute em um ambiente de testes, sem sobrescrever a conexão H2 nem fornecer credenciais `ADMIN_EMAIL`/`ADMIN_PASSWORD` de uso real, pois os testes limpam registros.

A suíte cobre regras de negócio, integração de vendas, fluxo REST, controle de acesso e inicialização do Spring. Inclui login, acesso protegido, cadastro de animal, pesagem, venda, relatórios e rollback. Isso não substitui testes no banco real ou a integração do navegador.

Para os testes de contrato do frontend, com Node.js instalado, a partir da raiz:

```powershell
node --test src/frontend/tests/contracts.test.cjs
```

Esses testes verificam contratos e comportamento local; não fazem chamadas à API.

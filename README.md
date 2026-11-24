# AgroBoi

Backend Java 21 com Spring Boot; frontend HTML/CSS/JavaScript.

## Banco e execução

Perfil padrão: `mysql` (use `SPRING_PROFILES_ACTIVE=mariadb` para MariaDB). Por padrão `DDL_AUTO=update`: o Hibernate cria as tabelas sozinho. Para rodar `banco/agroboi_mariadb.sql` manualmente, defina `DDL_AUTO=validate`.

Variáveis de ambiente (nenhuma senha vai para arquivos versionados):

| Variável | Para quê |
|---|---|
| `DB_PASSWORD` | senha do banco (obrigatória); `DB_USER` e `DB_URL` são opcionais |
| `JWT_SECRET` | chave do JWT, mínimo 32 caracteres. Sem ela, uma chave aleatória é criada a cada start (logins caem ao reiniciar) |
| `ADMIN_EMAIL` / `ADMIN_PASSWORD` | cria o primeiro administrador no start, se o e-mail ainda não existir |
| `CORS_ORIGENS` | origens liberadas (padrão `*`) |

Em `src/backend/AgroBoi`: `./mvnw spring-boot:run` (Windows: `./mvnw.cmd spring-boot:run`). Swagger UI: `http://localhost:8080/swagger-ui.html` (botão Authorize aceita o token do login).

## API REST

Tudo em `/api`, JSON. Exceto login e cadastro, exige `Authorization: Bearer <token>`.

| Recurso | Endpoints | Requisitos |
|---|---|---|
| Auth | `POST /auth/cadastro`, `POST /auth/login`, `GET /auth/me` | RF01, RF02 |
| Usuários (só ADMINISTRADOR) | `GET /usuarios`, `GET/PUT/DELETE /usuarios/{id}` (DELETE desativa) | |
| Fazenda | `GET /fazenda`, `PUT /fazenda` (fazenda única, ID 1) | |
| Raças | CRUD `/racas` | |
| Animais | CRUD `/animais`; `GET /animais?busca=&racaId=&situacao=&sexo=` | RF03, RF04, RF05, RF15 |
| Registros sanitários | CRUD `/registros-sanitarios?animalId=&tipo=VACINA\|MEDICAMENTO\|PROCEDIMENTO` | RF06, RF07 |
| Alimentação | CRUD `/alimentacoes?animalId=` | RF08 |
| Pesagens | CRUD `/pesagens?animalId=` | RF09 |
| Reprodução | CRUD `/reproducoes?matrizId=` | RF10 |
| Despesas | CRUD `/despesas?animalId=` | RF11 |
| Vendas | `GET /vendas`, `GET /vendas/{id}`, `POST /vendas` (venda + itens + saídas) | RF11, RF12 |
| Itens de venda | `GET /itens-venda?vendaId=`, `GET /itens-venda/{id}` | |
| Movimentações | `GET /movimentacoes?animalId=`, `GET /movimentacoes/{id}`, `POST /movimentacoes` | |
| Dashboard | `GET /dashboard` | RF13 |
| Relatórios | `GET /relatorios/rebanho`, `/vacinacao`, `/pesagens`, `/reproducao`, `/financeiro` (`?inicio=AAAA-MM-DD&fim=AAAA-MM-DD`) | RF14 |

Erros vêm como `{timestamp, status, mensagem}`: 400 regra de negócio ou JSON inválido, 401 sem login ou token inválido, 403 sem permissão, 404 não encontrado, 409 duplicado ou registro em uso.

Pelas regras de negócio, `DELETE` de animais, movimentações e vendas responde 400 (histórico é preservado). Receitas do financeiro = valor das vendas.

## Estado da implementação

Backend: entidades, services, controllers, segurança JWT, dashboard e relatórios implementados. Frontend: páginas ainda usam dados locais (localStorage) e `api.js` está vazio; falta ligar login, animais, saúde, alimentação, pesagem, reprodução, financeiro e dashboard à API. Backup (RNF11) não está implementado na aplicação (use `mysqldump` no contêiner do banco).

## Testes

Execute `./mvnw.cmd test` com Java instalado e o banco de teste configurado por variáveis de ambiente. O teste contextLoads depende da conexão e do esquema existente.

Validação: compilação Java concluída com JDK 23 (release 21); sintaxe JavaScript conferida. O teste contextLoads falhou ao obter metadados JDBC; integração com banco pendente de validação.


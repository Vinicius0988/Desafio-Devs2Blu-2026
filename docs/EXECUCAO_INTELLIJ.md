# Execução do AgroBoi no IntelliJ IDEA

Este guia configura o backend do repositório **Desafio-Devs2Blu-2026**. Os valores locais pertencem à configuração de execução do IntelliJ, não aos arquivos Java. Nenhuma migração de dados é realizada pela troca de perfil.

## 1. Preparar Java, Maven e projeto

1. Instale um JDK 21 ou superior. O projeto compila para Java 21.
2. Abra no IntelliJ a pasta `src/backend/AgroBoi` ou importe seu `pom.xml` como projeto Maven.
3. Em **File > Project Structure > Project**, selecione o JDK em **Project SDK**. O módulo também deve usar esse SDK.
4. Em **Settings > Build, Execution, Deployment > Build Tools > Maven**, selecione **Use Maven wrapper**. O repositório fixa a versão do Maven.
5. Nas opções de **Importing** e **Runner** do Maven, selecione o mesmo JDK para importação e execução.
6. Na janela Maven, use **Reload All Maven Projects** e aguarde o download das dependências.

O JDK usado para executar a aplicação pode ser diferente do usado pelo próprio IntelliJ; confira a configuração de execução. Referências: [JDKs no IntelliJ](https://support.jetbrains.com/hc/en-us/articles/40252201177618-Which-JDK-is-IntelliJ-IDEA-using-for-your-project) e [execução Maven](https://www.jetbrains.com/help/idea/run-debug-configuration-maven.html).

## 2. Preparar o banco

Inicie o MariaDB e confirme endereço, porta, usuário e senha. O exemplo local usa `localhost:3306` e banco `agroboi`. Se apenas MySQL estiver instalado, siga a alternativa MySQL abaixo.

Para um banco novo de desenvolvimento, execute em seu cliente SQL, com uma conta autorizada:

```sql
CREATE DATABASE IF NOT EXISTS agroboi
    CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Use uma conta com acesso a esse banco. Em desenvolvimento com `update`, ela precisa poder criar/alterar tabelas. A configuração do projeto não cria contas de banco nem inicia o serviço MariaDB.

Se já houver dados em MySQL, selecionar MariaDB não os copia. Não execute scripts de criação ou migração sobre dados existentes sem planejar a alteração. A compatibilidade do script manual com o servidor real ainda faz parte da validação do banco; consulte [Banco](../banco/README.md).

### Banco local já existente (XAMPP)

Na verificação de 26/11/2026, o servidor local era MariaDB 10.4.32 do XAMPP, acessível em `127.0.0.1:3306`, com banco `agroboi`, usuário `root` e senha vazia. As 12 tabelas e os nomes de suas colunas correspondiam ao SQL; naquele momento estavam vazias. Esse é o ambiente local verificado, não uma configuração padrão para outros computadores.

No painel do XAMPP, o serviço aparece como **MySQL**, mesmo sendo MariaDB. Inicie esse serviço antes do backend. Não é necessário recriar o banco existente. Para conferir o esquema sem modificá-lo, use `DDL_AUTO=validate`.

## 3. Criar a configuração de execução

1. Abra **Run > Edit Configurations**.
2. Adicione uma configuração **Application** chamada `AgroBoi - MariaDB dev`. Se houver suporte Spring Boot na sua edição, esse tipo também pode ser usado.
3. Informe a classe principal `AgroBoi.AgroBoiApplication` e selecione o módulo do backend no classpath.
4. Selecione JDK 21 ou superior em **JRE/SDK** e a pasta `src/backend/AgroBoi` como diretório de trabalho.
5. Abra **Environment variables**. Se o campo estiver oculto, procure-o em **Modify options**.
6. Abra o editor em formato de tabela e cadastre **uma variável por linha**, separando nome e valor.
7. Todas as variáveis da seção seguinte pertencem a **Environment variables**, não a **Program arguments** nem a **VM options**. Se a lista tiver sido colada em Program arguments, remova-a desse campo e transfira-a inteira.
8. Clique em **Apply > OK**, pare a execução anterior e execute pela configuração que acabou de editar. Se usar uma configuração Maven para iniciar a aplicação, as variáveis precisam estar nessa configuração Maven.

Os nomes das opções podem variar entre versões do IntelliJ.

Referências: [configuração Application](https://www.jetbrains.com/help/idea/run-debug-configuration-java-application.html) e [variáveis de ambiente](https://www.jetbrains.com/help/idea/program-arguments-and-environment-variables.html).

## 4. Preencher as variáveis

| Nome | Valor a informar | Necessidade |
|---|---|---|
| `SPRING_PROFILES_ACTIVE` | `mariadb,dev` | Recomendado explicitamente; é a combinação padrão sem perfis ativos |
| `DB_URL` | `jdbc:mariadb://localhost:3306/agroboi` | Opcional se usar esse endereço local |
| `DB_USER` | Seu usuário do MariaDB | Obrigatório |
| `DB_PASSWORD` | A senha desse usuário | Obrigatório; não há senha padrão no repositório |
| `JWT_SECRET` | Um segredo aleatório estável com no mínimo 32 bytes UTF-8 | Obrigatório |
| `ADMIN_EMAIL` | Email escolhido para o fazendeiro | Primeiro provisionamento, junto com a senha |
| `ADMIN_PASSWORD` | Senha de login escolhida para o fazendeiro | Primeiro provisionamento; é diferente da senha do banco |
| `CORS_ORIGENS` | `http://localhost:5500,http://127.0.0.1:5500` | Esse é o padrão de desenvolvimento; ajuste à porta real do frontend |
| `PORT` | `8080` | Opcional |
| `JWT_EXPIRACAO_MINUTOS` | `480` | Opcional; inteiro positivo |
| `DDL_AUTO` | `update` | Opcional em `dev`; use `validate` para conferir um esquema já preparado |

Preencha os valores no editor de variáveis, sem aspas adicionais. Não coloque credenciais em **Program arguments**, no `pom.xml` ou em arquivos versionados. Mantenha a configuração local e não habilite **Store as project file** com segredos. O `.gitignore` exclui `.idea`, `.run` e arquivos `.env`, mas isso não remove arquivos já rastreados pelo Git.

A aplicação **não carrega arquivos `.env` automaticamente**. Para esse procedimento, use o editor de variáveis do IntelliJ. Remova configurações antigas como `SPRING_DATASOURCE_PASSWORD` e argumentos `--spring.datasource.*` se desejar usar exclusivamente os nomes `DB_*`; propriedades diretas do Spring podem sobrescrever as variáveis documentadas.

### Exemplo para copiar no ambiente local verificado

Prefira o editor em tabela. Se usar o campo de texto em linha única, separe os pares por **ponto e vírgula**:

```text
SPRING_PROFILES_ACTIVE=mariadb,dev;DB_URL=jdbc:mariadb://127.0.0.1:3306/agroboi;DB_USER=root;DB_PASSWORD=;DDL_AUTO=validate;JWT_SECRET=SUBSTITUA_PELO_NOVO_SEGREDO;ADMIN_EMAIL=SUBSTITUA_PELO_SEU_EMAIL;ADMIN_PASSWORD=SUBSTITUA_PELA_SENHA_DO_LOGIN;CORS_ORIGENS=http://localhost:5500,http://127.0.0.1:5500;PORT=8080;JWT_EXPIRACAO_MINUTOS=480
```

Substitua os três valores iniciados por `SUBSTITUA` antes de executar. `DB_PASSWORD` deve existir com valor vazio nesse ambiente; não preencha com aspas, espaços ou a palavra `null`. `ADMIN_PASSWORD` é a senha que você escolhe para entrar no AgroBoi, diferente da senha do banco. Use um email seu em `ADMIN_EMAIL`; atualmente não há confirmação por email. Não use a senha da sua conta de email.

Confira especialmente estas duas linhas no editor:

| Nome | Valor |
|---|---|
| `PORT` | `8080` |
| `JWT_EXPIRACAO_MINUTOS` | `480` |

No formato de texto, o trecho deve ser `PORT=8080;JWT_EXPIRACAO_MINUTOS=480`. Sem o separador, a porta recebe um texto inválido e a aplicação não inicia. Para valores contendo ponto e vírgula, use o editor em tabela em vez de concatenar a linha manualmente.

### Gerar o segredo JWT

Execute no PowerShell local e copie o resultado para `JWT_SECRET` na configuração do IntelliJ:

```powershell
$jwtBytes = New-Object byte[] 48
$jwtRandom = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$jwtRandom.GetBytes($jwtBytes)
[Convert]::ToBase64String($jwtBytes)
$jwtRandom.Dispose()
```

Guarde esse valor privadamente e reutilize-o nas próximas execuções. Não gere uma chave nova a cada início. Trocar a chave invalida tokens existentes e exige novo login. Não use a chave fixa presente nos recursos de teste fora dos testes. Se compartilhar seu segredo em conversa, captura de tela ou repositório, gere outro para uso real.

## 5. Executar e acessar

1. Selecione `AgroBoi - MariaDB dev` e pressione **Run**.
2. Confira no console os perfis `mariadb` e `dev` e a mensagem `Started AgroBoiApplication`. Se faltar uma variável obrigatória, corrija a configuração antes de repetir.
3. Abra [Swagger UI](http://localhost:8080/swagger-ui.html), ajustando a porta se necessário.
4. Execute `POST /api/auth/login` com o email e a senha de `ADMIN_EMAIL`/`ADMIN_PASSWORD`.
5. Copie somente o valor do campo `token`, sem aspas, clique em **Authorize**, cole em **Value** sem escrever `Bearer`, confirme em **Authorize** e feche em **Close**. O login não autoriza automaticamente o Swagger; após esse passo ele envia o cabeçalho nas chamadas protegidas. Nas chamadas fora do Swagger, use `Authorization: Bearer <token>`.
6. Teste `GET /api/auth/me`, seguido de `GET /api/animais`. Uma lista vazia é normal em um banco novo.

O administrador só é criado quando ambas as variáveis estão preenchidas e o email não existe. Conta comum existente não é promovida; senha existente não é redefinida. Após o provisionamento, você pode remover `ADMIN_EMAIL` e `ADMIN_PASSWORD` da configuração: a conta permanece no banco. O cadastro pela API está bloqueado.

## 6. Frontend e CORS

Sirva o frontend por HTTP com um servidor estático. Se tiver Python instalado, execute a partir da raiz do repositório:

```powershell
python -m http.server 5500 --bind 127.0.0.1 --directory src/frontend
```

Abra `http://127.0.0.1:5500`. O endereço deve coincidir com uma entrada de `CORS_ORIGENS`; `localhost` e `127.0.0.1` são origens distintas. Informe somente protocolo, host e porta, separados por vírgula, sem caminho ou barra final. `*`, padrões com curingas, `null` e `file://` não são aceitos.

As telas ainda não consomem a API. Configurar CORS prepara a comunicação, mas não implementa essa integração. O Swagger servido pelo próprio backend não depende da origem do servidor estático.

## 7. MySQL e produção

| Uso | Perfis | Observação |
|---|---|---|
| MariaDB local | `mariadb,dev` | Padrão sem configuração explícita |
| MySQL local | `mysql,dev` | Use `DB_URL=jdbc:mysql://localhost:3306/agroboi` ou omita a URL para usar esse padrão |
| MariaDB em produção | `mariadb,prod` | Esquema previamente preparado; apenas validação |
| MySQL em produção | `mysql,prod` | Mesma política de validação |

Duplique a configuração no IntelliJ se precisar alternar entre bancos. Selecione **um perfil de banco e um de ambiente**; não combine `mysql` com `mariadb`, nem `dev` com `prod`. Perfis explícitos substituem os padrões: somente `mysql`, por exemplo, não ativa `dev` e usa `validate` da configuração base. Consulte [perfis Spring Boot](https://docs.spring.io/spring-boot/reference/features/profiles.html).

Em `prod`, `CORS_ORIGENS` deve ser informado e `DDL_AUTO` não é usado: o projeto configura `validate`. Informe endereço e credenciais reais por ambiente. Não sobrescreva `spring.jpa.hibernate.ddl-auto` diretamente se quiser manter essa proteção. A aplicação não configura HTTPS, backups, serviço do banco ou migrations automaticamente.

## 8. Executar testes no IntelliJ

1. Crie uma configuração **Maven** separada para a pasta do backend.
2. Informe o comando `test`, selecione o Maven Wrapper e o JDK do projeto.
3. Não copie variáveis de conexão ou credenciais da configuração da aplicação para essa configuração de testes.
4. Execute. Os testes Spring ativam `test`, usam H2 em memória e têm um segredo JWT exclusivo de testes. Não é necessário iniciar MariaDB.

Os valores `DB_*`, `ADMIN_*` e `JWT_SECRET` da aplicação não são necessários à suíte. Não sobrescreva diretamente propriedades Spring de conexão nos testes, pois isso pode trocar o banco em memória por um banco real.

Alternativa no terminal, dentro da pasta do backend:

```powershell
./mvnw.cmd -B -ntp test
```

## 9. Solução de problemas

| Mensagem ou comportamento | Verificar |
|---|---|
| `release version 21 not supported` | JDK do projeto, Maven Runner e configuração de execução; não use Java 11 |
| Dependências não resolvidas | Importação pelo `pom.xml`, Maven Wrapper, JDK do importador e recarga Maven |
| Placeholder `DB_USER` ou `DB_PASSWORD` não resolvido | Variáveis na configuração efetivamente selecionada no Run |
| `Access denied` com usuário literal `${DB_USER}` | A variável não chegou à execução. Mova a lista de Program arguments para Environment variables e confira a configuração selecionada; não é evidência de senha errada do `root` |
| `Access denied` com usuário correto | Usuário, senha e permissões no banco selecionado. No XAMPP verificado, `DB_PASSWORD` tem valor vazio |
| `Unable to determine Dialect` após falha de autenticação JDBC | Resolva primeiro a conexão; nesse caso, o erro de dialect é consequência da falta de metadados e não exige configurar o dialect manualmente |
| `Failed to bind properties under server.port` com `8080JWT_EXPIRACAO_MINUTOS=480` | Duas variáveis foram concatenadas. Separe em linhas distintas ou use `PORT=8080;JWT_EXPIRACAO_MINUTOS=480` |
| `Connection refused` | Serviço do banco, endereço e porta; o Spring não inicia o banco |
| `Unknown database` | Crie `agroboi` ou ajuste o nome em `DB_URL` |
| Falta `JWT_SECRET` ou segredo curto | Gere e configure uma chave estável com pelo menos 32 bytes |
| Erro de `CORS_ORIGENS` | Origens completas sem curingas, caminhos ou barra final; obrigatório em `prod` |
| Navegador bloqueia a requisição | Origem e porta reais do frontend e cabeçalho de autenticação |
| `Schema-validation` | Banco ou esquema diferente do esperado; valide a estrutura antes de usar `prod` |
| Login retorna 401 | Credenciais, conta ativa e provisionamento; variáveis de admin não alteram conta existente |
| Operação retorna 403 | Perfil ADMINISTRADOR e origem permitida; cadastro pela API permanece bloqueado |
| Porta HTTP ocupada | Finalize a execução anterior ou configure outro `PORT` |

Configurar variáveis apenas no terminal não altera uma instância do IntelliJ que já estava aberta. Preencha-as diretamente na configuração de execução ou reinicie a IDE após mudar o ambiente do sistema.

## 10. Confirmação da execução local

Em 26/11/2026, o responsável pelo projeto confirmou que a aplicação iniciou após mover as variáveis de Program arguments para Environment variables e separar `PORT` de `JWT_EXPIRACAO_MINUTOS`.

A conexão e a estrutura básica do banco foram verificadas por consultas de leitura. Na mesma sessão, o usuário confirmou manualmente pelo Swagger:

- Login por `POST /api/auth/login`.
- Acesso a `GET /api/auth/me` e `GET /api/animais`, após informar o token em Authorize. Antes disso, as chamadas retornavam 401.
- Criação e listagem de raça por `POST /api/racas` e `GET /api/racas`.
- Sucesso no teste solicitado de cadastro de animal; não foram registrados IDs ou conteúdo das respostas nesta documentação.

Essas confirmações demonstram autenticação e operações básicas no banco real; não substituem uma validação completa. Próxima etapa: testar pesagens, saúde, alimentação, reprodução, despesas e vendas, incluindo rollback e concorrência. A integração das telas com a API continua pendente. Nenhuma senha ou token é registrado aqui.

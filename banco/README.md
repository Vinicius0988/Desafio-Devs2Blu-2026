# Banco AgroBoi — MariaDB

O arquivo [agroboi_mariadb.sql](agroboi_mariadb.sql) contém a versão simplificada do banco, para MariaDB 10.6 ou superior.

Para entender a função de cada tabela e campo, consulte o [dicionário de dados](DICIONARIO_DE_DADOS.md).

Foram mantidas as 12 tabelas e os campos da modelagem, incluindo as referências de mãe e pai. O arquivo [agroboi_mer.mwb](agroboi_mer.mwb) é o modelo editável do MySQL Workbench; seus nomes de tabelas e colunas foram comparados com o SQL nesta revisão e coincidem. Essa comparação não valida tipos, restrições ou execução do esquema. Foram removidas as validações extensas em SQL, a chave estrangeira composta e os índices adicionais de consulta.

O banco utiliza apenas:

- IDs automáticos e chaves primárias; a fazenda tem ID fixo 1.
- Chaves estrangeiras simples, incluindo `animal.mae_id` e `animal.pai_id`.
- Campos obrigatórios e valores padrão.
- Unicidade de email, identificação do animal e nome da raça.
- Unicidade do animal na mesma venda e de movimentação por item vendido.
- Uma restrição curta, `CHECK (id = 1)`, para permitir somente uma fazenda.

As chaves estrangeiras bloqueiam a exclusão de registros referenciados. O MariaDB cria os índices necessários para essas chaves. Não há triggers, procedures ou índices adicionais de relatórios.

## Como executar

Abra o arquivo em um editor SQL conectado ao MariaDB e execute em um banco novo. O script cria e seleciona o banco `agroboi`; o usuário conectado precisa ter permissão para isso.

Depois, confira:

```sql
USE agroboi;
SHOW TABLES;
DESCRIBE animal;
```

O script deve ser executado uma vez. Ele não atualiza tabelas de uma versão anterior nem apaga dados existentes. Se a versão anterior já foi executada, será necessária uma migration para alterar aquele banco.

## Configuração da aplicação

O backend oferece perfis de banco `mariadb` (padrão) e `mysql`, combinados com `dev` ou `prod`. Sem perfis explícitos, usa `mariadb,dev`. O script desta pasta é destinado ao MariaDB; não é uma migration automática nem é executado ao iniciar o Spring.

No perfil `dev`, com `DDL_AUTO=update`, o Hibernate gera/ajusta tabelas a partir das entidades. Isso não garante a reprodução de todos os detalhes do script: por exemplo, o `CHECK (id = 1)` é explícito no SQL, enquanto o serviço também impõe a fazenda única. Enums e valores padrão podem ter representações diferentes.

Para usar o esquema manual, execute o script e configure `SPRING_PROFILES_ACTIVE=mariadb,dev` e `DDL_AUTO=validate` (ou `mariadb,prod`, que já usa `validate`). A validação real de compatibilidade ainda está pendente. Não há ferramenta de migrations configurada.

A conexão usa `DB_URL` e credenciais obrigatórias `DB_USER`/`DB_PASSWORD`. Não há senha fixa no repositório. Veja o [passo a passo no IntelliJ](../docs/EXECUCAO_INTELLIJ.md). A troca do driver não transfere dados entre servidores.

## O que fica no backend

Os services validam valores positivos, campos vazios, formatos, tipos, datas e genealogia. Também impedem que um animal seja seu próprio pai ou mãe e verificam o sexo dos genitores.

Ao vender um animal, `VendaService` salva venda, itens, saídas e situação do animal na mesma transação, validando que a movimentação pertence ao mesmo animal do item e que ele está disponível para venda. O SQL simplificado não faz essas verificações automaticamente.

Alimentação sem `animal_id` refere-se ao rebanho inteiro. Despesa sem `animal_id` é geral da fazenda. Cadastre a fazenda com `id = 1` e dados reais; armazene somente o hash da senha do fazendeiro.

## Validação

Estrutura, campos e referências foram conferidos com o SQL e o código. A suíte automatizada usa H2 em modo MariaDB, incluindo testes REST e rollback; ela não executa este script. A execução do SQL e a compatibilidade com servidor MariaDB real ainda precisam ser verificadas. Não há validação de banco real nesta revisão documental.

## Regras implementadas no backend

Consulte [Regras de negócio dos services](REGRAS_NEGOCIO.md) para validações, transações, decisões de histórico e execução dos testes.

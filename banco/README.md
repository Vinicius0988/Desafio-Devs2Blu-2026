# Banco AgroBoi — MariaDB

O arquivo [agroboi_mariadb.sql](agroboi_mariadb.sql) contém a versão simplificada do banco, para MariaDB 10.6 ou superior.

Para entender a função de cada tabela e campo, consulte o [dicionário de dados](DICIONARIO_DE_DADOS.md).

Foram mantidas as 12 tabelas e os campos da modelagem, preservando as funcionalidades previstas e os diagramas UML. Foram removidas as validações extensas em SQL, a chave estrangeira composta e os índices adicionais de consulta.

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

## O que fica no backend

O Spring deve validar valores positivos, campos vazios, formatos, tipos, perfis, datas e genealogia. Também deve impedir que um animal seja seu próprio pai ou mãe e verificar o sexo dos genitores.

Ao vender um animal, o serviço deve salvar venda, itens, saídas e situação do animal na mesma transação, validando que a movimentação pertence ao mesmo animal do item e que ele está disponível para venda. O SQL simplificado não faz essas verificações automaticamente.

Alimentação sem `animal_id` refere-se ao rebanho inteiro. Despesa sem `animal_id` é geral da fazenda. Cadastre a fazenda com `id = 1` e dados reais; armazene somente o hash da senha do fazendeiro.

## Validação

Estrutura, campos e referências conferidos localmente. A execução ainda não foi testada em servidor MariaDB, indisponível neste ambiente.

# AgroBoi

Backend Java 21 com Spring Boot; frontend HTML/CSS/JavaScript.

## Banco e execução

Crie as tabelas com `banco/agroboi_mariadb.sql` antes de iniciar. O Hibernate valida o esquema, sem modificá-lo automaticamente.

Defina `DB_PASSWORD` e, se necessário, `DB_USER` e `DB_URL`. Não armazene senhas nos arquivos versionados.

Em `src/backend/AgroBoi`, execute `./mvnw.cmd spring-boot:run` no Windows. MariaDB é o perfil padrão. Para MySQL, defina `SPRING_PROFILES_ACTIVE=mysql`; para MariaDB, `SPRING_PROFILES_ACTIVE=mariadb`.

Os dois drivers estão disponíveis, mas apenas um banco é utilizado por execução. Dois servidores locais precisam de portas distintas; ajuste `DB_URL`. Isso não replica nem sincroniza dados. A execução do esquema em MySQL ainda precisa ser validada.

## Estado da implementação

As entidades e enums seguem o dicionário de dados. O frontend contém páginas iniciais; login e cadastro aguardam endpoints e integração. Os botões estão desabilitados para evitar envios sem implementação. Raça e genitores são referências por ID; a seleção amigável depende da API. Peso pertence a pesagem e idade é calculada pelo nascimento.

## Testes

Execute `./mvnw.cmd test` com Java instalado e o banco de teste configurado por variáveis de ambiente. O teste contextLoads depende da conexão e do esquema existente.

Validação: compilação Java concluída com JDK 23 (release 21); sintaxe JavaScript conferida. O teste contextLoads falhou ao obter metadados JDBC; integração com banco pendente de validação.


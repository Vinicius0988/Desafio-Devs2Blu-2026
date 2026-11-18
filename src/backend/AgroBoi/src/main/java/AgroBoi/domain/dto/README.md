# DTOs do domínio

`request` contém os dados recebidos pela API; `response`, os dados devolvidos ao Angular. São records Java, sem anotações JPA e sem dependência das entidades.

- IDs e datas de criação são retornados, não recebidos para cadastro. Em atualizações, o ID deve vir pela rota.
- Relacionamentos são representados por IDs, incluindo raça, mãe e pai; os serviços devem buscar e validar as entidades correspondentes.
- Venda recebe uma lista de ItemVendaRequest com animalId e valor. O serviço associa os itens à venda dentro da mesma transação. VendaResponse inclui seus itens.
- UsuarioRequest recebe nome, email e senha; o backend deve gerar o hash e definir perfil e situação com autorização apropriada. UsuarioResponse nunca retorna senha ou hash. O toString da entrada omite as credenciais.
- Campos opcionais podem ser nulos. Validações de obrigatoriedade, tamanho, valores positivos e coerência deverão ser aplicadas na camada de serviço ou com Bean Validation ao implementar os endpoints.
- Estes DTOs não implementam login, conversão automática de entidades ou endpoints.

Os pacotes seguem as pastas: AgroBoi.domain.dto.request e AgroBoi.domain.dto.response.

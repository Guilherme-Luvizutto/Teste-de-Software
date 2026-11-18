# Testes de Integração com Spring Boot e JUnit 5

Projeto desenvolvido como atividade prática de **Testes de Integração**, utilizando Spring Boot, Spring Data JPA, banco de dados H2, JUnit 5 e MockMvc.

## Tecnologias utilizadas

- Java 17 (versão indicada no enunciado)
- Maven
- Spring Boot 3.x
- Spring Data JPA
- H2 Database
- JUnit 5
- MockMvc
- Jackson / ObjectMapper

## Objetivo

Validar a integração entre as camadas da aplicação, verificando as regras de negócio, a persistência dos dados no banco H2 e o funcionamento dos endpoints HTTP por meio de requisições simuladas.

A atividade foi dividida em dois exercícios:

1. Integração entre Service, Repository e banco de dados H2.
2. Integração HTTP utilizando MockMvc.

---

## Exercício 1 — Integração entre Service, Repository e H2

**Classe de teste:** `ProdutoServiceIntegrationTest`

Foram implementados três casos de teste:

| Caso | Descrição | Resultado esperado |
|---|---|---|
| CT01 | Cadastro de um Teclado com preço 150.0 | Produto cadastrado com ID e recuperado corretamente pelo Repository. |
| CT02 | Tentativa de cadastro com nome em branco e preço 80.0 | Lançamento de `IllegalArgumentException`, sem aumento na quantidade de registros. |
| CT03 | Cadastro de um Mouse por 80.0 e atualização para 95.0 | Repository retorna o produto com o preço atualizado. |

### Recursos utilizados

- `@SpringBootTest`: carrega o contexto da aplicação para executar o teste de integração.
- `@Autowired`: injeta as dependências reais do Spring.
- `@Transactional`: mantém os testes dentro de uma transação, normalmente revertida ao final de cada método.
- `assertNotNull`: verifica se o ID e o produto recuperado não são nulos.
- `assertEquals`: compara os valores esperados com os valores obtidos.
- `assertThrows`: verifica se uma exceção é lançada.

### Discussão

**1. Por que consultar com `repository.findById()` fornece uma evidência diferente de verificar apenas o objeto retornado pelo serviço?**

Porque verificar somente o objeto retornado pelo serviço confirma que ele possui os dados esperados, mas não comprova, por si só, que o registro foi persistido corretamente. Ao consultar diretamente o Repository, verificamos se o produto realmente pode ser recuperado da camada de persistência.

**2. O que `@Transactional` faz ao término de cada teste?**

O Spring normalmente realiza o rollback da transação ao final de cada método de teste. Isso evita que os dados criados durante um teste interfiram nos demais, mantendo o banco isolado entre as execuções.

---

## Exercício 2 — Integração HTTP com MockMvc

**Classe de teste:** `ProdutoControllerIntegrationTest`

Foram implementados dois casos de teste:

| Caso | Descrição | Resultado esperado |
|---|---|---|
| CT04 | Cadastro de um Mouse por meio de POST `/produtos` | HTTP 200 e JSON contendo ID numérico, nome e preço corretos. |
| CT05 | Cadastro por POST, extração do ID e consulta por GET `/produtos/{id}` | HTTP 200 e JSON com os dados correspondentes ao produto cadastrado. |

### Recursos utilizados

- `MockMvc`: executa requisições HTTP simuladas.
- `post()`: realiza uma requisição POST para cadastrar o produto.
- `get()`: realiza uma requisição GET para consultar o produto.
- `MediaType.APPLICATION_JSON`: informa que o conteúdo enviado está no formato JSON.
- `status().isOk()`: verifica se a resposta possui HTTP 200.
- `jsonPath()`: verifica os valores presentes no JSON de resposta.
- `ObjectMapper` e `JsonNode`: permitem interpretar o JSON e extrair o ID retornado pelo cadastro.

### Discussão

**1. Quais componentes são efetivamente exercitados?**

São exercitadas as camadas Controller, Service e Repository, além da integração com o banco H2. Dessa forma, o teste verifica o fluxo completo da aplicação, desde a requisição até a persistência e recuperação dos dados.

**2. Por que MockMvc não precisa iniciar um servidor HTTP real?**

Porque o MockMvc simula as requisições HTTP diretamente no ambiente de testes do Spring, utilizando o mecanismo de processamento das requisições da aplicação, sem precisar iniciar um servidor externo para receber as chamadas.

**3. Qual a diferença entre esse teste e um teste unitário do Controller com dependências simuladas?**

No teste de integração com MockMvc, as dependências reais da aplicação são utilizadas, permitindo verificar a comunicação entre as camadas. Já em um teste unitário com mocks, as dependências são substituídas por objetos simulados, verificando principalmente o comportamento isolado do Controller.

---

## Resultado da execução

Comando utilizado:

```bash
mvn test
```

**Resultado obtido na execução da suíte completa:**

```text
Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
-- in br.edu.exemplo.ProdutoServiceIntegrationTest

Tests run: 2, Failures: 0, Errors: 0, Skipped: 0
-- in br.edu.exemplo.ProdutoControllerIntegrationTest

Tests run: 5, Failures: 0, Errors: 0, Skipped: 0

BUILD SUCCESS

Total time: 14.768 s
```

### Resumo dos resultados

| Classe | Testes executados | Falhas |
|---|---:|---:|
| ProdutoServiceIntegrationTest | 3 | 0 |
| ProdutoControllerIntegrationTest | 2 | 0 |
| **Total** | **5** | **0** |

**Status final: todos os cinco testes foram executados com sucesso.**

---

## Isolamento dos testes

O projeto utiliza o banco H2 em memória para realizar os testes de persistência, evitando a necessidade de um banco de dados externo.

Além disso, a anotação `@Transactional` permite que as operações realizadas durante cada teste sejam revertidas ao seu término, evitando que os registros criados em um teste interfiram nos demais.

Não foram utilizados mocks para substituir `ProdutoRepository` ou `ProdutoService`, conforme solicitado no enunciado.

## Como executar

É necessário ter Java e Maven configurados no ambiente.

Na pasta que contém o arquivo `pom.xml`, execute:

```bash
mvn test
```

O Maven compilará o projeto e executará automaticamente os testes de integração.

---

## Conclusão

A atividade permitiu verificar, na prática, a integração entre as camadas de uma aplicação Spring Boot.

Os testes demonstraram o funcionamento das regras de cadastro e atualização, a persistência dos dados no H2 e a comunicação entre Controller, Service e Repository por meio de requisições HTTP simuladas.

Ao final, os cinco casos de teste foram executados sem falhas ou erros, confirmando o funcionamento dos cenários implementados.
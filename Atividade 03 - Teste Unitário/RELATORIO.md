# Relatório do grupo

Integrantes: ____________________________________

## Grafos e complexidade

Modelo adotado: cada decisão `if`, `while` e cada alternativa relevante do `switch` é considerada no CFG. Nas condições com `&&`/`||`, cada operando de curto-circuito foi tratado como decisão separada. Exceções foram representadas como saídas de erro no desenho conceitual, mas não como branches do JaCoCo.

### Grafo de chamadas de `PedidoService.fechar`

`fechar` → valida referências → `Pedido.subtotalCentavos` → `Pedido.estoqueSuficiente` → `PoliticaDesconto.calcular` → `CalculadoraFrete.calcular` → `Pedido.pesoGramas` / `Pedido.temFragil` → `AnaliseRisco.avaliar` → `PagamentoService.pagar` → `ProcessadorPagamento.autorizar`.

Há retornos antecipados para `BLOQUEADO`, `SEM_ESTOQUE` e `REVISAO`, além das exceções de validação.

### CFGs resumidos

**AnaliseRisco.avaliar:** entrada → valida total → bloqueado? → histórico zero? → regra de total/expresso ou regra de total/VIP → `REVISAO`/`APROVADO`.

**CalculadoraFrete.calcular:** entrada → valida líquido → `switch` da UF → calcula excedente → `while` do peso → frete grátis? → VIP? → expresso? → frágil? → retorno.

**PoliticaDesconto.calcular:** entrada → valida subtotal → desconto VIP/comum/zero → cupom nulo/branco? → `switch` do cupom → elegibilidade do cupom → teto de 20% → retorno.

**PagamentoService.pagar:** entrada → valida total → valida limite → `do/while` → tentativa → autorização → sucesso/recusa ou `IllegalStateException` → nova tentativa enquanto houver limite → `false`.

**PedidoService.fechar:** valida referências → bloqueado? → subtotal → estoque → desconto → frete → risco → aprovado? → pagamento → resultado.

### Complexidade ciclomática

| Método | Nós | Arestas | V(G) | Caminhos independentes (base) | Restrições de viabilidade |
| --- | ---: | ---: | ---: | ---: | --- |
| `AnaliseRisco.avaliar` | — | — | 9 | 9 | Alguns caminhos de risco só são alcançáveis diretamente na unidade |
| `CalculadoraFrete.calcular` | — | — | 10 | 10 | O laço gera muitos caminhos; foram usados zero/uma/várias iterações |
| `PoliticaDesconto.calcular` | — | — | 12 | 12 | Combinações de cupons e teto não são todas enumeradas |
| `PagamentoService.pagar` | — | — | 8 | 8 | Número de tentativas limitado a 1–3 |
| `PedidoService.fechar` | — | — | 8 | 8 | Retornos antecipados impedem alguns caminhos de colaboração |
| `Pedido.subtotalCentavos` | — | — | 2 | 2 | Item ativo/inativo |
| `Pedido.pesoGramas` | — | — | 2 | 2 | Lista vazia/uma ou mais linhas |
| `Pedido.temFragil` | — | — | 3 | 3 | Item inativo, ativo não frágil e ativo frágil |
| `Pedido.estoqueSuficiente` | — | — | 3 | 3 | Estoque suficiente ou `break` no primeiro item indisponível |

Os valores de V(G) acima seguem o modelo de curto-circuito declarado. A relação usada é `V(G) = E − N + 2` para cada CFG conectado; como o exercício permite representar o mesmo grafo com blocos diferentes, a contagem de nós/arestas deve ser ajustada caso o desenho do grupo use outra granularidade, mantendo a mesma interpretação das decisões.

## Matriz de testes

| ID / método JUnit | Unidade | Entrada e estado do stub | Resultado esperado | Caminho / aresta | Critério atendido |
| --- | --- | --- | --- | --- | --- |
| `rejeitaClienteBloqueado` | Risco | bloqueado | `RECUSADO` | retorno antecipado | ramo bloqueado |
| `revisaNovoClientePorTotalAlto` | Risco | histórico 0, total 100001 | `REVISAO` | total alto | limite |
| `revisaNovoClientePorEntregaExpressa` | Risco | histórico 0, expresso | `REVISAO` | segundo operando do `||` | curto-circuito |
| `revisaClienteComHistoricoNaoVipPorTotalAlto` | Risco | histórico 1, não VIP, total 500001 | `REVISAO` | `&&` verdadeiro | ramo |
| `aprovaClienteVipMesmoComTotalAlto` | Risco | histórico 1, VIP | `APROVADO` | `!vip` falso | curto-circuito |
| `cobraPesoExcedentePorFracao` | Frete | peso 3001 g | + R$ 3,00 | `while` uma iteração | laço/limite |
| `freteGratisAcimaDe300ReaisNaEntregaNormal` | Frete | líquido 30000, normal | `0` | gratuidade | limite |
| `adicionaExpressoEFragilUmaUnicaVez` | Frete | expresso + 2 itens frágeis | base + R$15 + R$5 | adicionais | combinação |
| `vipRecebeDezPorCento` | Desconto | VIP, subtotal 10000 | 1000 | ramo VIP | desconto |
| `bemVindoElegivelSomaVinteReais` | Desconto | histórico 0, subtotal 10000, cupom com espaços/minúsculas | 3000 | cupom BEMVINDO | normalização |
| `descontoLimitadoA20PorCento` | Desconto | VIP + BEMVINDO | 2000 | teto | limite |
| `cupomDesconhecidoLancaExcecao` | Desconto | cupom desconhecido | `IllegalArgumentException` | `default` | exceção |
| `calculaTotalEDisponibilidade` | Item | preço 2500, qtd 3, estoque 5 | total 7500, disponível | método auxiliar | método/linha |
| `rejeitaPrecoForaDoIntervalo` | Item | preço 0 e 1000001 | exceção | validações | limites |
| `copiaListaECalculaSubtotalIgnorandoInativos` | Pedido | linha ativa + inativa | subtotal 2000 | `continue` | laço |
| `identificaFragilApenasEmItemAtivo` | Pedido | frágil inativo/ativo | false/true | condição composta | curto-circuito |
| `indisponibilidadePodeSerSeguidaDeSucesso` | Pagamento | 1 falha temporária + sucesso | `true`, 2 chamadas | `do/while` | repetição |
| `esgotaTentativasEmIndisponibilidade` | Pagamento | 3 falhas temporárias | `false`, 3 chamadas | saída do laço | limite |
| `deveFecharPedidoDeClienteComumComFreteDoParanaEPagamentoAprovado` | Serviço | pedido normal, pagamento aprovado | `PAGO`, total 11200 | caminho completo | colaboração |
| `bloqueadoRetornaZeroSemCobrarMesmoComCupomInvalido` | Serviço | cliente bloqueado + cupom inválido | `BLOQUEADO`, sem cobrança | retorno antecipado | ordem |
| `faltaEstoqueRetornaSemEstoqueSemCobrarNemValidarCupom` | Serviço | estoque insuficiente + cupom inválido | `SEM_ESTOQUE`, sem cobrança | retorno antecipado | ordem |
| `riscoEmRevisaoNaoCobra` | Serviço | novo cliente, total alto | `REVISAO`, sem cobrança | risco | colaboração |
| `pagamentoRecusadoRetornaValoresCalculados` | Serviço | processador retorna false | `PAGAMENTO_RECUSADO` | pagamento | resultado |
| `pagamentoPodeTentarAteTresVezes` | Serviço | processador indisponível | recusado, 3 chamadas | retry | estado do stub |

## Evolução da cobertura

| Etapa | Testes executados | Linhas | Branches | Métodos | Classes | Lacunas e justificativas |
| --- | --- | --- | --- | --- | --- | --- |
| Inicial | 1 (`PedidoServiceTest`) | medida pelo projeto | medida pelo projeto | medida pelo projeto | medida pelo projeto | Apenas o caminho feliz fornecido |
| Final | suíte das classes de teste | executar `mvn clean test` para atualizar | executar `mvn clean test` para atualizar | executar `mvn clean test` para atualizar | executar `mvn clean test` para atualizar | Caminhos completos de laços não são enumerados; exceções não contam como branch no JaCoCo |

## Análise crítica

- Cobertura de ramos não demonstra cobertura de caminhos completos: por exemplo, o frete pode ter os ramos de VIP, expresso e fragilidade cobertos separadamente sem testar a combinação específica entre eles.
- Condições de curto-circuito foram testadas com entradas que fazem o segundo operando ser necessário ou desnecessário, como risco com `total > 100000 || expresso` e desconto com `&&`.
- Há caminhos possíveis em `AnaliseRisco` que são viáveis quando a classe é testada isoladamente, mas podem não ser alcançados por `PedidoService.fechar`, porque o serviço possui retornos antecipados.
- Exceções de validação foram verificadas com `assertThrows`. O pagamento também verifica a diferença entre `IllegalStateException`, que permite nova tentativa, e outras exceções, que são propagadas.
- O laço de pagamento foi exercitado com uma tentativa, duas tentativas e três tentativas. O laço de peso foi exercitado sem excedente e com excedente por fração.
- A alteração proposital recomendada pelo roteiro deve ser feita apenas temporariamente, por exemplo alterando uma tarifa ou limite usado por um teste, executar a suíte para observar a falha e desfazer a alteração antes da entrega.

### Observação sobre execução

Os testes foram escritos para JUnit 5 e o código de produção foi compilado com JDK 21. Nesta preparação o ambiente de execução não possui o comando `mvn` instalado, portanto os números finais do JaCoCo devem ser obtidos na máquina do aluno executando:

```bash
mvn clean test
```

Depois, consultar `target/site/jacoco/index.html` e preencher a tabela acima com os percentuais apresentados pelo JaCoCo.

# language: pt
@dynamic-catalog @dynamic-lazy-load @regression
Funcionalidade: Dynamic Catalog - Lazy Load
  Como cliente da Swag Labs
  Quero que mais produtos apareçam conforme eu rolo a lista
  Para navegar pelo catálogo sem trocar de página

  # Rolar até o fim carrega mais 6 cards (lista infinita).
  # Um card só mostra o produto quando aparece na tela; antes disso mostra "Loading…" (com reticências, um caractere só).
  # A quantidade inicial de cards depende do tamanho da janela, por isso os cenários não supõem um número fixo.

  Contexto:
    Dado que estou logado com o usuário padrão
    E abro o Lazy Load pelo menu Dynamic Catalog

  Cenário: LZY01 - Mais cards são carregados ao rolar até o fim
    Então a lista deve começar com pelo menos 6 cards
    Quando rolo até o fim da lista
    Então devem entrar pelo menos 6 cards novos
    E o total de cards deve ser múltiplo de 6

  Cenário: LZY02 - Card só carrega quando aparece na tela
    Quando deixo um card logo abaixo da área visível da tela
    Então esse card deve mostrar o texto "Loading…"
    Quando rolo a tela até esse card
    Então esse card deve carregar o produto

  # Conteúdo dos cards: a lista tem 22 itens (com tamanhos) e recomeça depois do último.
  # Análise de Valor Limite: início da lista, fim do 1º lote, início do 2º lote,
  # último item da lista e o primeiro depois dele (a lista recomeça).
  Esquema do Cenário: <id> - <descricao>
    Quando rolo a lista até o card da posição <posicao>
    Então o card da posição <posicao> deve mostrar o produto "<nome>" por <preco>

    Exemplos:
      | id    | descricao                              | posicao | nome                          | preco  |
      | LZY03 | Primeiro card da lista                 | 0       | Sauce Labs Bike Light         | $9.99  |
      | LZY04 | Último card do primeiro lote           | 5       | Sauce Labs Fleece Jacket (XS) | $49.99 |
      | LZY05 | Primeiro card carregado pela rolagem   | 6       | Sauce Labs Fleece Jacket (S)  | $49.99 |
      | LZY06 | Último item da lista                   | 21      | Sauce Labs Bolt T-Shirt (S)   | $15.99 |
      | LZY07 | A lista recomeça depois do último item | 22      | Sauce Labs Bike Light         | $9.99  |
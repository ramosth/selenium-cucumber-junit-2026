# language: pt
@inventory @inventory-detalhes
Funcionalidade: Detalhes do produto
  Como cliente da Swag Labs
  Quero ver os detalhes de um produto e adicioná-lo ao carrinho por lá
  Para decidir a compra com todas as informações

  # Valores esperados (nome, id, preço e descrição): enum data/Product.java
  # Usuário padrão: todos os produtos. Outros usuários: o primeiro e o último produto (limites da faixa de ids 0 a 5).
  # Defeitos de propósito (@expected-failure):
  #   problem_user e error_user: abrem o produto errado / falhas no carrinho
  #   visual_user: preço exibido na lista é aleatório

  Esquema do Cenário: <id> - Detalhes do produto <produto> - <usuario>
    Dado que estou logado com o usuário <usuario>
    Então na lista de produtos, o produto <produto> deve mostrar o preço do cadastro
    Quando abro os detalhes do produto <produto>
    Então a página de detalhes deve ser do produto <produto>
    E a página de detalhes deve mostrar o nome, a descrição, o preço e a imagem do produto <produto>

    @regression
    Exemplos: Usuário padrão, todos os produtos
      | id   | usuario  | produto       |
      | PD01 | STANDARD | BACKPACK      |
      | PD02 | STANDARD | BIKE_LIGHT    |
      | PD03 | STANDARD | BOLT_T_SHIRT  |
      | PD04 | STANDARD | FLEECE_JACKET |
      | PD05 | STANDARD | ONESIE        |
      | PD06 | STANDARD | RED_T_SHIRT   |

    @regression
    Exemplos: performance_glitch_user (passa, com lentidão)
      | id   | usuario            | produto       |
      | PD13 | PERFORMANCE_GLITCH | BIKE_LIGHT    |
      | PD14 | PERFORMANCE_GLITCH | FLEECE_JACKET |

    @expected-failure
    Exemplos: Usuários com defeito nos detalhes (falha esperada)
      | id   | usuario | produto       |
      | PD07 | PROBLEM | BIKE_LIGHT    |
      | PD08 | PROBLEM | FLEECE_JACKET |
      | PD09 | ERROR   | BIKE_LIGHT    |
      | PD10 | ERROR   | FLEECE_JACKET |
      | PD11 | VISUAL  | BIKE_LIGHT    |
      | PD12 | VISUAL  | FLEECE_JACKET |

  Esquema do Cenário: <id> - Adicionar e remover pela página de detalhes: <produto> - <usuario>
    Dado que estou logado com o usuário <usuario>
    E abro os detalhes do produto <produto>
    Quando adiciono o produto ao carrinho pela página de detalhes
    Então o botão Remove deve aparecer na página de detalhes
    E o contador do carrinho deve ser 1
    Quando removo o produto do carrinho pela página de detalhes
    Então o botão Remove não deve aparecer na página de detalhes
    E o contador do carrinho deve ser 0
    Quando volto para a lista de produtos pela página de detalhes
    Então devo estar na lista de produtos

    @regression
    Exemplos: Usuário padrão, todos os produtos
      | id    | usuario  | produto       |
      | ADR01 | STANDARD | BACKPACK      |
      | ADR02 | STANDARD | BIKE_LIGHT    |
      | ADR03 | STANDARD | BOLT_T_SHIRT  |
      | ADR04 | STANDARD | FLEECE_JACKET |
      | ADR05 | STANDARD | ONESIE        |
      | ADR06 | STANDARD | RED_T_SHIRT   |

    @regression
    Exemplos: visual_user e performance_glitch_user (passam)
      | id    | usuario            | produto       |
      | ADR11 | VISUAL             | BIKE_LIGHT    |
      | ADR12 | VISUAL             | FLEECE_JACKET |
      | ADR13 | PERFORMANCE_GLITCH | BIKE_LIGHT    |
      | ADR14 | PERFORMANCE_GLITCH | FLEECE_JACKET |

    @expected-failure
    Exemplos: problem_user e error_user (falha esperada)
      | id    | usuario | produto       |
      | ADR07 | PROBLEM | BIKE_LIGHT    |
      | ADR08 | PROBLEM | FLEECE_JACKET |
      | ADR09 | ERROR   | BIKE_LIGHT    |
      | ADR10 | ERROR   | FLEECE_JACKET |
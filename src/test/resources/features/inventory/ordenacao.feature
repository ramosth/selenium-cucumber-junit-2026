# language: pt
@inventory @inventory-ordenacao
Funcionalidade: Ordenação da lista de produtos
  Como cliente da Swag Labs
  Quero ordenar os produtos por nome ou por preço
  Para encontrar mais rápido o que procuro

  # Os cenários verificam o comportamento CORRETO, com cada usuário do Sauce Demo.
  # Defeitos de propósito (@expected-failure):
  #   problem_user: a ordenação não muda nada
  #   error_user:   abre um alerta e não ordena
  #   visual_user:  os preços exibidos são aleatórios (falha nas ordenações por preço)

  Esquema do Cenário: <id> - Ordenação padrão por nome (A a Z) - <usuario>
    Dado que estou logado com o usuário <usuario>
    Então a ordenação selecionada deve ser "Name (A to Z)"
    E os produtos devem estar em ordem alfabética de A a Z

    @regression
    Exemplos: Todos os usuários
      | id    | usuario            |
      | ORD01 | STANDARD           |
      | ORD02 | PROBLEM            |
      | ORD03 | PERFORMANCE_GLITCH |
      | ORD04 | ERROR              |
      | ORD05 | VISUAL             |

  Esquema do Cenário: <id> - Ordenar por nome (Z a A) - <usuario>
    Dado que estou logado com o usuário <usuario>
    Quando ordeno os produtos por "Name (Z to A)"
    Então não deve aparecer alerta de erro
    E a ordenação selecionada deve ser "Name (Z to A)"
    E os produtos devem estar em ordem alfabética de Z a A

    @regression
    Exemplos: Usuários sem defeito na ordenação por nome
      | id    | usuario            |
      | ORD06 | STANDARD           |
      | ORD07 | PERFORMANCE_GLITCH |
      | ORD08 | VISUAL             |

    @expected-failure
    Exemplos: Usuários com defeito na ordenação (falha esperada)
      | id    | usuario |
      | ORD09 | PROBLEM |
      | ORD10 | ERROR   |

  Esquema do Cenário: <id> - Ordenar por preço (menor para maior) - <usuario>
    Dado que estou logado com o usuário <usuario>
    Quando ordeno os produtos por "Price (low to high)"
    Então não deve aparecer alerta de erro
    E a ordenação selecionada deve ser "Price (low to high)"
    E os preços devem estar do menor para o maior

    @regression
    Exemplos: Usuários sem defeito na ordenação por preço
      | id    | usuario            |
      | ORD11 | STANDARD           |
      | ORD12 | PERFORMANCE_GLITCH |

    @expected-failure
    Exemplos: Usuários com defeito na ordenação (falha esperada)
      | id    | usuario |
      | ORD13 | PROBLEM |
      | ORD14 | ERROR   |
      | ORD15 | VISUAL  |

  Esquema do Cenário: <id> - Ordenar por preço (maior para menor) - <usuario>
    Dado que estou logado com o usuário <usuario>
    Quando ordeno os produtos por "Price (high to low)"
    Então não deve aparecer alerta de erro
    E a ordenação selecionada deve ser "Price (high to low)"
    E os preços devem estar do maior para o menor

    @regression
    Exemplos: Usuários sem defeito na ordenação por preço
      | id    | usuario            |
      | ORD16 | STANDARD           |
      | ORD17 | PERFORMANCE_GLITCH |

    @expected-failure
    Exemplos: Usuários com defeito na ordenação (falha esperada)
      | id    | usuario |
      | ORD18 | PROBLEM |
      | ORD19 | ERROR   |
      | ORD20 | VISUAL  |
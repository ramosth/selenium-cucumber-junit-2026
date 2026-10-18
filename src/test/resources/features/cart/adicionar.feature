# language: pt
@cart @cart-adicionar @regression
Funcionalidade: Adicionar produtos ao carrinho
  Como cliente da Swag Labs
  Quero adicionar produtos ao carrinho
  Para comprar vários itens de uma vez

  # Produtos pela chave da constante em data/Product.java (o nome completo fica só lá)

  Contexto:
    Dado que estou logado com o usuário padrão

  Cenário: CAR01 - Adicionar um produto ao carrinho
    Quando adiciono ao carrinho os produtos:
      | BACKPACK |
    Então o contador do carrinho deve ser 1
    E o carrinho deve conter os produtos:
      | BACKPACK |

  Cenário: CAR02 - Adicionar dois produtos ao carrinho
    Quando adiciono ao carrinho os produtos:
      | BACKPACK   |
      | BIKE_LIGHT |
    Então o contador do carrinho deve ser 2
    E o carrinho deve conter os produtos:
      | BACKPACK   |
      | BIKE_LIGHT |

  Cenário: CAR03 - Adicionar todos os produtos ao carrinho
    Quando adiciono ao carrinho os produtos:
      | BACKPACK      |
      | BIKE_LIGHT    |
      | BOLT_T_SHIRT  |
      | FLEECE_JACKET |
      | ONESIE        |
      | RED_T_SHIRT   |
    Então o contador do carrinho deve ser 6
    E o carrinho deve conter os produtos:
      | BACKPACK      |
      | BIKE_LIGHT    |
      | BOLT_T_SHIRT  |
      | FLEECE_JACKET |
      | ONESIE        |
      | RED_T_SHIRT   |
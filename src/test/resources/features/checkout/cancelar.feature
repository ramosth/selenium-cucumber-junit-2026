# language: pt
@checkout @checkout-cancelar @regression
Funcionalidade: Desistir da compra sem perder o carrinho
  Como cliente da Swag Labs
  Quero poder voltar atrás durante a compra
  Para revisar o pedido sem perder os produtos que escolhi

  # Cancel nos dados do comprador -> volta ao carrinho
  # Cancel no resumo do pedido    -> volta à lista de produtos
  # Continue Shopping no carrinho -> volta à lista de produtos

  Contexto:
    Dado que estou logado com o usuário padrão
    E que adicionei ao carrinho os produtos:
      | BACKPACK   |
      | BIKE_LIGHT |

  Cenário: CNL01 - Cancel nos dados do comprador volta ao carrinho
    Quando sigo para o checkout
    E cancelo nos dados do comprador
    Então devo estar na página do carrinho
    E o carrinho deve conter os produtos:
      | BACKPACK   |
      | BIKE_LIGHT |

  Cenário: CNL02 - Cancel no resumo volta aos produtos
    Quando sigo para o checkout
    E preencho os dados do comprador com nome "Maria", sobrenome "Silva" e CEP "20000-000"
    E clico em Continue
    E cancelo no resumo do pedido
    Então devo estar na lista de produtos
    E o contador do carrinho deve ser 2

  Cenário: CNL03 - Continue Shopping volta aos produtos
    Quando abro o carrinho
    E volto para a lista de produtos pelo botão Continue Shopping
    Então o contador do carrinho deve ser 2
    E na lista de produtos, o produto BACKPACK deve estar marcado como adicionado
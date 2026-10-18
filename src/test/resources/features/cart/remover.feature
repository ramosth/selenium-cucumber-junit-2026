# language: pt
@cart @cart-remover @regression
Funcionalidade: Remover produtos do carrinho
  Como cliente da Swag Labs
  Quero remover produtos do carrinho
  Para comprar só o que eu realmente quero

  Contexto:
    Dado que estou logado com o usuário padrão

  Cenário: RMV01 - Remover pela lista de produtos
    Dado que adicionei ao carrinho os produtos:
      | BACKPACK      |
      | BIKE_LIGHT    |
      | FLEECE_JACKET |
    Quando removo o produto BACKPACK pela lista de produtos
    Então o contador do carrinho deve ser 2
    E na lista de produtos, o produto BACKPACK deve estar disponível para adicionar
    E o carrinho deve conter os produtos:
      | BIKE_LIGHT    |
      | FLEECE_JACKET |

  Cenário: RMV02 - Remover pela página do carrinho
    Dado que adicionei ao carrinho os produtos:
      | BACKPACK   |
      | BIKE_LIGHT |
    Quando removo o produto BACKPACK pela página do carrinho
    Então o carrinho deve conter os produtos:
      | BIKE_LIGHT |
    E o contador do carrinho deve ser 1
    Quando volto para a lista de produtos pelo botão Continue Shopping
    Então na lista de produtos, o produto BACKPACK deve estar disponível para adicionar

  Cenário: RMV03 - Remover todos os produtos
    Dado que adicionei ao carrinho os produtos:
      | BACKPACK   |
      | BIKE_LIGHT |
    Quando removo o produto BACKPACK pela página do carrinho
    E removo o produto BIKE_LIGHT pela página do carrinho
    Então o carrinho deve estar vazio
    E o contador do carrinho deve ser 0
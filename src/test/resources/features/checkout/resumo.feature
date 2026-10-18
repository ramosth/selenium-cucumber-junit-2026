# language: pt
@checkout @checkout-resumo @regression
Funcionalidade: Cálculo do resumo do pedido
  Como cliente da Swag Labs
  Quero ver o valor dos itens, a taxa e o total antes de concluir
  Para saber exatamente quanto vou pagar

  # Regras: Item total = soma dos preços | Tax = 8% do item total (2 casas) | Total = item total + tax
  # Cenários: o item mais barato e o mais caro (limites de preço), 2 e 3 itens, e todos os 6 (limite de quantidade)

  Contexto:
    Dado que estou logado com o usuário padrão

  Cenário: CALC01 - Resumo com 1 item, o mais barato
    Dado que adicionei ao carrinho os produtos:
      | ONESIE |
    Quando sigo para o checkout
    E preencho os dados do comprador com nome "Maria", sobrenome "Silva" e CEP "20000-000"
    E clico em Continue
    Então o resumo do pedido deve conter os produtos:
      | ONESIE |
    E o resumo do pedido deve mostrar item total $7.99, taxa $0.64 e total $8.63

  Cenário: CALC02 - Resumo com 1 item, o mais caro (taxa arredonda para cima)
    Dado que adicionei ao carrinho os produtos:
      | FLEECE_JACKET |
    Quando sigo para o checkout
    E preencho os dados do comprador com nome "Maria", sobrenome "Silva" e CEP "20000-000"
    E clico em Continue
    Então o resumo do pedido deve conter os produtos:
      | FLEECE_JACKET |
    E o resumo do pedido deve mostrar item total $49.99, taxa $4.00 e total $53.99

  Cenário: CALC03 - Resumo com 2 itens
    Dado que adicionei ao carrinho os produtos:
      | BACKPACK   |
      | BIKE_LIGHT |
    Quando sigo para o checkout
    E preencho os dados do comprador com nome "Maria", sobrenome "Silva" e CEP "20000-000"
    E clico em Continue
    Então o resumo do pedido deve conter os produtos:
      | BACKPACK   |
      | BIKE_LIGHT |
    E o resumo do pedido deve mostrar item total $39.98, taxa $3.20 e total $43.18

  Cenário: CALC04 - Resumo com 3 itens, dois com o mesmo preço
    Dado que adicionei ao carrinho os produtos:
      | BOLT_T_SHIRT |
      | RED_T_SHIRT  |
      | ONESIE       |
    Quando sigo para o checkout
    E preencho os dados do comprador com nome "Maria", sobrenome "Silva" e CEP "20000-000"
    E clico em Continue
    Então o resumo do pedido deve conter os produtos:
      | BOLT_T_SHIRT |
      | RED_T_SHIRT  |
      | ONESIE       |
    E o resumo do pedido deve mostrar item total $39.97, taxa $3.20 e total $43.17

  Cenário: CALC05 - Resumo com todos os 6 produtos
    Dado que adicionei ao carrinho os produtos:
      | BACKPACK      |
      | BIKE_LIGHT    |
      | BOLT_T_SHIRT  |
      | FLEECE_JACKET |
      | ONESIE        |
      | RED_T_SHIRT   |
    Quando sigo para o checkout
    E preencho os dados do comprador com nome "Maria", sobrenome "Silva" e CEP "20000-000"
    E clico em Continue
    Então o resumo do pedido deve conter os produtos:
      | BACKPACK      |
      | BIKE_LIGHT    |
      | BOLT_T_SHIRT  |
      | FLEECE_JACKET |
      | ONESIE        |
      | RED_T_SHIRT   |
    E o resumo do pedido deve mostrar item total $129.94, taxa $10.40 e total $140.34
# language: pt
@checkout @checkout-recibo @regression
Funcionalidade: Recibo do pedido em PDF
  Como cliente da Swag Labs
  Quero baixar o recibo do meu pedido em PDF
  Para guardar o comprovante da compra

  # Nome do arquivo: data e hora do pedido em UTC (swag-labs-order-AAAA-MM-DD_HH-MM-SS.pdf)
  # Dentro do PDF: data e hora no horário local, em inglês (October 15, 2026 at 6:52 PM)

  Contexto:
    Dado que estou logado com o usuário padrão

  Cenário: PDF01 - Recibo com 1 item, o mais barato
    Dado que adicionei ao carrinho os produtos:
      | ONESIE |
    E que concluí a compra com nome "Maria", sobrenome "Silva" e CEP "20000-000"
    Quando gero o recibo em PDF
    Então o nome do arquivo deve ter a data e hora do pedido em UTC
    E o PDF deve conter os textos fixos do recibo
    E o PDF deve conter o comprador "Maria Silva" e o CEP "20000-000"
    E o PDF deve conter os itens com os preços:
      | ONESIE |
    E o PDF deve mostrar item total $7.99, taxa $0.64 e total $8.63
    E o PDF deve mostrar a data do pedido no horário local

  Cenário: PDF02 - Recibo com 1 item, o mais caro
    Dado que adicionei ao carrinho os produtos:
      | FLEECE_JACKET |
    E que concluí a compra com nome "Maria", sobrenome "Silva" e CEP "20000-000"
    Quando gero o recibo em PDF
    Então o nome do arquivo deve ter a data e hora do pedido em UTC
    E o PDF deve conter os textos fixos do recibo
    E o PDF deve conter o comprador "Maria Silva" e o CEP "20000-000"
    E o PDF deve conter os itens com os preços:
      | FLEECE_JACKET |
    E o PDF deve mostrar item total $49.99, taxa $4.00 e total $53.99
    E o PDF deve mostrar a data do pedido no horário local

  Cenário: PDF03 - Recibo com 2 itens
    Dado que adicionei ao carrinho os produtos:
      | BACKPACK   |
      | BIKE_LIGHT |
    E que concluí a compra com nome "Maria", sobrenome "Silva" e CEP "20000-000"
    Quando gero o recibo em PDF
    Então o nome do arquivo deve ter a data e hora do pedido em UTC
    E o PDF deve conter os textos fixos do recibo
    E o PDF deve conter o comprador "Maria Silva" e o CEP "20000-000"
    E o PDF deve conter os itens com os preços:
      | BACKPACK   |
      | BIKE_LIGHT |
    E o PDF deve mostrar item total $39.98, taxa $3.20 e total $43.18
    E o PDF deve mostrar a data do pedido no horário local

  Cenário: PDF04 - Recibo com 3 itens, dois com o mesmo preço
    Dado que adicionei ao carrinho os produtos:
      | BOLT_T_SHIRT |
      | RED_T_SHIRT  |
      | ONESIE       |
    E que concluí a compra com nome "Maria", sobrenome "Silva" e CEP "20000-000"
    Quando gero o recibo em PDF
    Então o nome do arquivo deve ter a data e hora do pedido em UTC
    E o PDF deve conter os textos fixos do recibo
    E o PDF deve conter o comprador "Maria Silva" e o CEP "20000-000"
    E o PDF deve conter os itens com os preços:
      | BOLT_T_SHIRT |
      | RED_T_SHIRT  |
      | ONESIE       |
    E o PDF deve mostrar item total $39.97, taxa $3.20 e total $43.17
    E o PDF deve mostrar a data do pedido no horário local

  Cenário: PDF05 - Recibo com todos os 6 produtos
    Dado que adicionei ao carrinho os produtos:
      | BACKPACK      |
      | BIKE_LIGHT    |
      | BOLT_T_SHIRT  |
      | FLEECE_JACKET |
      | ONESIE        |
      | RED_T_SHIRT   |
    E que concluí a compra com nome "Maria", sobrenome "Silva" e CEP "20000-000"
    Quando gero o recibo em PDF
    Então o nome do arquivo deve ter a data e hora do pedido em UTC
    E o PDF deve conter os textos fixos do recibo
    E o PDF deve conter o comprador "Maria Silva" e o CEP "20000-000"
    E o PDF deve conter os itens com os preços:
      | BACKPACK      |
      | BIKE_LIGHT    |
      | BOLT_T_SHIRT  |
      | FLEECE_JACKET |
      | ONESIE        |
      | RED_T_SHIRT   |
    E o PDF deve mostrar item total $129.94, taxa $10.40 e total $140.34
    E o PDF deve mostrar a data do pedido no horário local
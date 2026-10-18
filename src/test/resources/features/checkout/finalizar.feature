# language: pt
@checkout @checkout-finalizar @regression
Funcionalidade: Finalizar a compra
  Como cliente da Swag Labs
  Quero informar os meus dados e concluir o pedido
  Para receber os produtos que escolhi

  Contexto:
    Dado que estou logado com o usuário padrão
    E que adicionei ao carrinho os produtos:
      | BACKPACK |
    E sigo para o checkout

  Cenário: CHK01 - Finalizar compra com dados válidos
    Quando preencho os dados do comprador com nome "Maria", sobrenome "Silva" e CEP "20000-000"
    E clico em Continue
    Então o resumo do pedido deve conter os produtos:
      | BACKPACK |
    Quando clico em Finish
    Então devo ver a confirmação do pedido "Thank you for your order!"

  Esquema do Cenário: <id> - <descricao>
    Quando preencho os dados do comprador com nome "<nome>", sobrenome "<sobrenome>" e CEP "<cep>"
    E clico em Continue
    Então devo ver no checkout a mensagem de erro "<mensagem>"
    E devo continuar na tela de dados do comprador

    Exemplos:
      | id    | descricao                              | nome  | sobrenome | cep       | mensagem                       |
      | CHK02 | Checkout sem o primeiro nome           |       | Silva     | 20000-000 | Error: First Name is required  |
      | CHK03 | Checkout sem o sobrenome               | Maria |           | 20000-000 | Error: Last Name is required   |
      | CHK04 | Checkout sem o CEP                     | Maria | Silva     |           | Error: Postal Code is required |
      | CHK05 | Checkout com todos os campos em branco |       |           |           | Error: First Name is required  |
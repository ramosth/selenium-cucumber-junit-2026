# language: pt
@navigation @navigation-menu @regression
Funcionalidade: Menu lateral
  Como cliente da Swag Labs
  Quero usar o menu lateral
  Para navegar pela loja, sair da minha conta e limpar o carrinho

  Contexto:
    Dado que estou logado com o usuário padrão

  Cenário: MNU01 - Abrir e fechar o menu lateral
    Quando abro o menu lateral
    Então o menu lateral deve estar aberto
    Quando fecho o menu lateral
    Então o menu lateral deve estar fechado

  Cenário: MNU02 - All Items a partir do carrinho
    Quando abro o carrinho
    E clico em All Items no menu lateral
    Então devo estar na lista de produtos

  Cenário: MNU03 - About abre o site da Sauce Labs na mesma aba
    Então o link About deve apontar para "https://saucelabs.com/"
    E o link About deve estar configurado para abrir na mesma aba
    Quando clico em About no menu lateral
    Então devo estar no site "saucelabs.com"
    E nenhuma aba nova deve ter sido aberta

  Cenário: MNU04 - Logout e bloqueio de acesso sem login
    Quando faço logout
    E acesso a página "inventory.html" direto pela URL
    Então devo ver a mensagem de erro "Epic sadface: You can only access '/inventory.html' when you are logged in."
    E devo continuar na página de login

  Cenário: MNU05 - Reset App State esvazia o carrinho
    Dado que adicionei ao carrinho os produtos:
      | BACKPACK   |
      | BIKE_LIGHT |
    E o contador do carrinho deve ser 2
    Quando clico em Reset App State no menu lateral
    Então o contador do carrinho deve ser 0
    Quando fecho o menu lateral
    Então o carrinho deve estar vazio
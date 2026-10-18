# language: pt
@cart @cart-persistencia @regression
Funcionalidade: Persistência do carrinho
  Como cliente da Swag Labs
  Quero que o carrinho continue com os meus produtos
  Para não perder o que escolhi ao navegar, recarregar a página ou sair e entrar de novo

  # O Sauce Demo guarda o carrinho no navegador (localStorage); o logout só apaga a sessão.

  Contexto:
    Dado que estou logado com o usuário padrão
    E que adicionei ao carrinho os produtos:
      | BACKPACK   |
      | BIKE_LIGHT |

  Cenário: PER01 - Carrinho mantido ao navegar entre páginas
    Quando abro os detalhes do produto ONESIE
    E volto para a lista de produtos pela página de detalhes
    Então o contador do carrinho deve ser 2
    E na lista de produtos, o produto BACKPACK deve estar marcado como adicionado
    E na lista de produtos, o produto BIKE_LIGHT deve estar marcado como adicionado

  Cenário: PER02 - Carrinho mantido ao recarregar a página
    Quando recarrego a página
    Então o contador do carrinho deve ser 2
    E na lista de produtos, o produto BACKPACK deve estar marcado como adicionado
    E na lista de produtos, o produto BIKE_LIGHT deve estar marcado como adicionado

  Cenário: PER03 - Carrinho mantido após logout e login
    Quando faço logout
    E faço login com o usuário "standard_user" e a senha "secret_sauce"
    Então devo ver a página de produtos
    E o contador do carrinho deve ser 2
    E o carrinho deve conter os produtos:
      | BACKPACK   |
      | BIKE_LIGHT |
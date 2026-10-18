# language: pt
@dynamic-catalog @dynamic-spinner @regression
Funcionalidade: Dynamic Catalog - Spinner
  Como cliente da Swag Labs
  Quero ver um aviso de carregamento enquanto os produtos chegam
  Para saber que a página ainda está trabalhando

  # O spinner fica na tela por um tempo ALEATÓRIO (0,5 a 3 segundos) antes da grade com os produtos.
  # Por isso os passos usam espera explícita (esperar o spinner sumir), nunca Thread.sleep.

  Contexto:
    Dado que estou logado com o usuário padrão
    E abro o Spinner pelo menu Dynamic Catalog

  Cenário: SPN01 - Spinner aparece enquanto os produtos carregam
    Então o spinner deve aparecer enquanto os produtos carregam
    E o spinner deve sumir quando os produtos aparecerem

  Cenário: SPN02 - Todos os produtos aparecem após o carregamento
    Quando espero os produtos do Spinner carregarem
    Então a grade do Spinner deve mostrar 6 produtos

  Cenário: SPN03 - Nome e preço de cada produto
    Quando espero os produtos do Spinner carregarem
    Então a grade do Spinner deve mostrar o nome e o preço do cadastro dos produtos:
      | BIKE_LIGHT    |
      | BOLT_T_SHIRT  |
      | ONESIE        |
      | RED_T_SHIRT   |
      | BACKPACK      |
      | FLEECE_JACKET |

  Cenário: SPN04 - Spinner aparece de novo ao recarregar a página
    Quando espero os produtos do Spinner carregarem
    E recarrego a página do Spinner
    Então o spinner deve aparecer enquanto os produtos carregam
    E o spinner deve sumir quando os produtos aparecerem
    E a grade do Spinner deve mostrar 6 produtos
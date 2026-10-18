# language: pt
@dynamic-catalog @dynamic-slider @regression
Funcionalidade: Dynamic Catalog - Slider
  Como cliente da Swag Labs
  Quero ver os produtos em destaque passando sozinhos
  Para conhecer o catálogo sem precisar clicar

  # Um ponto para cada produto; a posição do ponto é o id do produto (data/Product.java).
  # O slider troca sozinho a cada 2 segundos e, depois do último, volta ao primeiro.

  Contexto:
    Dado que estou logado com o usuário padrão
    E abro o Slider pelo menu Dynamic Catalog

  Cenário: SLD01 - Um ponto para cada produto e só um ativo
    Então o slider deve ter 6 pontos
    E apenas 1 ponto deve estar ativo

  Cenário: SLD02 - Slider troca de produto sozinho
    Quando espero o slider trocar de produto sozinho
    Então o slider deve estar no ponto seguinte

  Cenário: SLD03 - Clicar no ponto mostra o produto certo
    Então cada ponto do slider deve mostrar o produto do cadastro:
      | BIKE_LIGHT    |
      | BOLT_T_SHIRT  |
      | ONESIE        |
      | RED_T_SHIRT   |
      | BACKPACK      |
      | FLEECE_JACKET |

  Cenário: SLD04 - Depois do último produto, volta ao primeiro
    Quando clico no ponto do produto FLEECE_JACKET
    E espero o slider trocar de produto sozinho
    Então o slider deve mostrar o produto BIKE_LIGHT
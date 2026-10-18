# language: pt
@navigation @navigation-rodape @regression
Funcionalidade: Rodapé
  Como cliente da Swag Labs
  Quero encontrar as redes sociais e as informações legais no rodapé
  Para acompanhar a Sauce Labs sem sair da loja

  # [ano atual] é trocado pelo ano de hoje no momento da execução

  Contexto:
    Dado que estou logado com o usuário padrão

  Cenário: FTR01 - Links das redes sociais
    Quando rolo até o rodapé
    Então o link do X deve apontar para "https://x.com/saucelabs"
    E o link do Facebook deve apontar para "https://www.facebook.com/saucelabs"
    E o link do LinkedIn deve apontar para "https://www.linkedin.com/company/sauce-labs/"
    E os links das redes sociais devem abrir em nova aba

  Cenário: FTR02 - Copyright com o ano atual
    Quando rolo até o rodapé
    Então o copyright deve ser "© [ano atual] Sauce Labs. All Rights Reserved. Terms of Service | Privacy Policy"

  Cenário: FTR03 - Rodapé também aparece no carrinho
    Quando abro o carrinho
    E rolo até o rodapé
    Então o link do X deve apontar para "https://x.com/saucelabs"
    E o link do Facebook deve apontar para "https://www.facebook.com/saucelabs"
    E o link do LinkedIn deve apontar para "https://www.linkedin.com/company/sauce-labs/"
    E os links das redes sociais devem abrir em nova aba
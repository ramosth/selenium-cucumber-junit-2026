# Selenium + Cucumber (BDD) + JUnit 5: Sauce Demo

Framework de automação de testes web em **Java + Selenium WebDriver + Cucumber (BDD) + JUnit 5**, seguindo o padrão **Page Object Model (POM)**.
A aplicação testada é o [Sauce Demo](https://www.saucedemo.com/), uma loja virtual de demonstração.

Este projeto é a versão **JUnit 5** do [selenium-cucumber-testng-2026](https://github.com/ramosth/selenium-cucumber-testng-2026) (Cucumber + TestNG): as features, os steps, os hooks, as páginas e as esteiras são os mesmos, e só o motor que executa os cenários mudou. A base veio do [selenium-automation-framework-2026](https://github.com/ramosth/selenium-automation-framework-2026) (Selenium + TestNG). O que mudou na troca está na seção [Do TestNG para o JUnit 5](#do-testng-para-o-junit-5).

**Recursos:**

- Cenários em **Gherkin em português** (`# language: pt`), com a user story no cabeçalho de cada funcionalidade.
- **110 cenários**: 92 na regressão e 18 que expõem os defeitos de propósito do Sauce Demo (`@expected-failure`).
- Runner do **JUnit Platform** (`@Suite` + motor do Cucumber), sem classe-mãe e sem suítes XML.
- Execução por **tags do JUnit** (`-Dgroups=login`, `-Dgroups="login & !login-invalido"`): os cenários fora do filtro nem entram no relatório.
- Verificações com o **`Assertions` do JUnit 5**, incluindo `assertAll` para mostrar todas as divergências de uma vez.
- **Tipos de parâmetro personalizados**: produtos, usuários e valores escritos no cenário como aparecem para o negócio (`BACKPACK`, `PROBLEM`, `$7.99`).
- Páginas **dinâmicas** (Dynamic Catalog) com espera explícita, nunca `Thread.sleep`.
- Validação de **arquivo baixado**: o recibo do pedido em PDF é baixado e tem o conteúdo conferido.
- Dois relatórios HTML: **Extent Reports** (por cenário, com prints) e o **relatório nativo do Cucumber** (passo a passo).
- Integração contínua com **GitHub Actions** e **Jenkins**, com o **e-mail do resultado enviado pelas esteiras** (o framework não tem código de e-mail).

---

## Tecnologias

| Ferramenta | Para que serve |
|---|---|
| Java 17 + Maven | Linguagem e gerenciador de build/dependências |
| Selenium WebDriver 4 | Controla o navegador |
| Cucumber 7 (`cucumber-java`) | Lê os arquivos `.feature` e liga cada frase a um método Java |
| JUnit 5: Platform + `junit-platform-suite` | Plataforma que descobre e executa os testes; o `@Suite` transforma o `TestRunner` numa suíte |
| `cucumber-junit-platform-engine` | Motor do Cucumber dentro da plataforma do JUnit |
| JUnit Jupiter (`junit-jupiter-api`) | Só a biblioteca de verificações (`Assertions`) |
| `junit-bom` | Lista de versões: alinha todas as peças do JUnit numa versão só |
| PicoContainer (`cucumber-picocontainer`) | Injeção de dependência: entrega o mesmo `TestContext` (navegador) aos Hooks e a todos os Steps |
| Apache PDFBox | Lê o texto do recibo em PDF baixado |
| Extent Reports | Relatório HTML por cenário, com prints |
| Log4j2 | Logs no console e em arquivo |
| GitHub Actions / Jenkins | Executam os cenários automaticamente (CI) e enviam o resultado por e-mail |

---

## Do requisito ao teste: o fluxo BDD

```
Requisito  →  User story  →  Cenários em Gherkin (.feature)  →  Steps (Java)  →  Páginas (Page Object)  →  Navegador
              Como/Quero/Para   "o quê", em português            "como"           locators + ações
```

Exemplo real (`features/login/login.feature`):

```gherkin
# language: pt
@login @regression
Funcionalidade: Login
  Como cliente da Swag Labs
  Quero entrar no sistema com o meu usuário e senha
  Para acessar o catálogo de produtos e fazer minhas compras

  Contexto:
    Dado que estou na página de login

  @login-invalido
  Esquema do Cenário: <id> - <descricao>
    Quando faço login com o usuário "<usuario>" e a senha "<senha>"
    Então devo ver a mensagem de erro "<mensagem>"
    E devo continuar na página de login

    Exemplos:
      | id   | descricao                   | usuario         | senha        | mensagem                                            |
      | CT06 | Login com usuário bloqueado | locked_out_user | secret_sauce | Epic sadface: Sorry, this user has been locked out. |
```

Cada frase corresponde a um método em `steps/`:

```java
@Entao("devo ver a mensagem de erro {string}")
public void devoVerAMensagemDeErro(String mensagemEsperada) {
    LoginPage loginPage = new LoginPage(context.getDriver());
    // JUnit: (esperado, atual, mensagem)
    Assertions.assertEquals(mensagemEsperada, loginPage.getErrorMessage(),
            "A mensagem de erro exibida é diferente da esperada.");
}
```

---

## Estrutura de pastas

```
selenium-cucumber-junit-2026/
├── .github/workflows/ci.yml        # GitHub Actions (tags do JUnit + e-mail pela esteira)
├── Jenkinsfile                     # Pipeline do Jenkins (tags do JUnit + e-mail pelo Email Extension)
├── pom.xml                         # Dependências (junit-bom) e Surefire
├── .vscode/settings.json           # Configuração da extensão do Gherkin (passos em português)
├── src/main/java/com/test/automation/      # FRAMEWORK (reaproveitado do projeto Selenium + TestNG)
│   ├── config/ConfigReader.java            # Lê o config.properties (ou -Dchave=valor)
│   ├── data/                               # Product (nome, id, preço, descrição) e User (perfis do site)
│   ├── driver/DriverFactory.java           # Cria o navegador (chrome/firefox/edge, headless, downloads)
│   ├── pages/                              # Page Objects + components/ (Header e Footer)
│   ├── reports/ExtentReportManager.java    # Instância única do Extent Report
│   └── utils/                              # Log, prints, downloads e leitura de PDF
├── src/main/resources/log4j2.xml
├── src/test/java/com/test/automation/      # CAMADA BDD
│   ├── runners/TestRunner.java             # @Suite: entrega as features ao motor do Cucumber
│   ├── context/TestContext.java            # "Mochila" do cenário: navegador + registro no Extent
│   ├── hooks/Hooks.java                    # Antes/depois de cada cenário (substitui o BaseTest)
│   └── steps/                              # Tradução das frases em ações e validações
│       ├── ParameterTypes.java             # {produto}, {usuario}, {valor}
│       ├── LoginSteps.java
│       ├── CartSteps.java
│       ├── CheckoutSteps.java
│       ├── ProductSteps.java
│       ├── NavigationSteps.java
│       └── DynamicCatalogSteps.java
└── src/test/resources/
    ├── junit-platform.properties           # Configuração do Cucumber (glue, plugins, nomes dos testes)
    ├── config.properties.example           # Modelo de configuração (versionado)
    └── features/                           # Os cenários em Gherkin
        ├── login/login.feature
        ├── cart/            adicionar · remover · persistencia
        ├── checkout/        finalizar · cancelar · resumo · recibo
        ├── inventory/       ordenacao · detalhes
        ├── navigation/      menu · rodape
        └── dynamic-catalog/ spinner · lazy-load · slider
```

Pastas geradas a cada execução e ignoradas pelo Git: `reports/`, `screenshots/`, `downloads/`, `logs/` e `target/`.

---

## Como as peças conversam

```
mvn test
  └─▶ Surefire                          encontra a classe TestRunner (padrão de nome "Test*")
        └─▶ JUnit Platform
              └─▶ Suite Engine          lê o @Suite do TestRunner
                    └─▶ Cucumber Engine lê os .feature da pasta "features"
                          │  Para CADA cenário, o PicoContainer cria um TestContext novo
                          │  e entrega o MESMO objeto aos Hooks e a todos os Steps
                          ├─▶ Hooks @Before    registra o cenário no Extent e abre o navegador
                          ├─▶ Steps            Dado / Quando / Então → Page Objects
                          └─▶ Hooks @After     print, resultado no Extent e fecha o navegador
```

O runner não estende nenhuma classe: quem dá o poder a ele são as anotações.

```java
@Suite
@IncludeEngines("cucumber")
@SelectPackages("features")
public class TestRunner {
    // Vazia de propósito: as anotações já fazem todo o trabalho
}
```

As configurações do Cucumber ficam em `src/test/resources/junit-platform.properties`, que o JUnit lê sozinho:

| Chave | Valor | Para quê |
|---|---|---|
| `cucumber.glue` | `com.test.automation.hooks,com.test.automation.steps` | Onde estão os Hooks e os Steps |
| `cucumber.plugin` | `pretty, html:reports/cucumber-report.html` | Passos no console + relatório do Cucumber |
| `cucumber.ansi-colors.disabled` | `true` | Console sem códigos de cor |
| `cucumber.junit-platform.naming-strategy` | `short` + `.short.example-name=pickle` | Nome dos testes no Surefire/Jenkins igual ao do cenário: `CT01 - Login com usuário padrão` |

| Peça | Papel | No projeto Selenium + TestNG era… |
|---|---|---|
| `.feature` | **O quê** testar, em português | A classe de teste |
| `TestRunner` | Entrega as features ao motor do Cucumber | As suítes XML |
| `TestContext` | Leva o navegador entre Hooks e Steps | O `driver` herdado do `BaseTest` |
| `Hooks` | `@BeforeAll` cria o Extent, `@Before`/`@After` por cenário, `@AfterAll` grava o Extent | O `BaseTest` |
| Steps | **Como** cada frase vira ação ou validação | O corpo dos métodos `@Test` |

**Regras dos Steps:**

- **Globais:** qualquer feature usa qualquer passo. O carrinho usa `que estou logado com o usuário padrão` (LoginSteps), o checkout usa `o carrinho deve conter os produtos:` (CartSteps).
- **Independentes:** cada passo cria a página de que precisa a partir do driver (`new CartPage(driver)`), então os passos podem ser combinados em qualquer ordem.
- **Sem Assert nas páginas:** as páginas executam ações e respondem perguntas; os Steps decidem com o `Assertions` do JUnit 5, sempre na ordem **(esperado, atual, mensagem)**.
- **Duas frases, uma ação:** `@Dado("que adicionei ao carrinho os produtos:")` e `@Quando("adiciono ao carrinho os produtos:")` no mesmo método.
- **Hooks do Cucumber, não do JUnit:** os Hooks usam `io.cucumber.java.BeforeAll`/`AfterAll`. O `org.junit.jupiter.api.BeforeAll` tem o mesmo nome, mas não é chamado pelo Cucumber.

---

## Escrevendo cenários

### Convenções

- **`# language: pt` na primeira linha.** Palavras-chave: `Funcionalidade`, `Contexto`, `Cenário`, `Esquema do Cenário`, `Exemplos`, `Dado`, `Quando`, `Então`, `E`, `Mas`.
- **User story** (Como / Quero / Para) logo abaixo da `Funcionalidade`.
- **ID no começo do nome do cenário** (`CT06 - Login com usuário bloqueado`). O ID vai para o nome do print e para o nome do teste no Jenkins.
- **Um cenário por linha** nos `Exemplos`, com o **resultado esperado explícito** (mensagens, valores, produtos).
- **Sem `if`/`switch` no código:** sucesso e erro ficam em esquemas separados, cada um com os seus passos de validação.
- **Cada linha de tabela tem o mesmo número de colunas do cabeçalho.**

### Tipos de parâmetro

| Tipo | Exemplo no `.feature` | O Java recebe | Onde está definido |
|---|---|---|---|
| `{string}` | `"standard_user"` | `String` | Cucumber |
| `{int}` | `6` | `int` | Cucumber |
| `{produto}` | `BACKPACK` | `Product.BACKPACK` | `ParameterTypes` |
| `{usuario}` | `PROBLEM` | `User.PROBLEM` | `ParameterTypes` |
| `{valor}` | `$7.99` | `7.99` | `ParameterTypes` |
| Tabela de produtos | `\| BACKPACK \|` | `List<Product>` | `ParameterTypes` (`@DataTableType`) |

- **Produtos pela constante:** o nome, o id, o preço e a descrição de cada produto existem só em `data/Product.java`. Se o site renomear um produto, só esse arquivo muda.
- **Por que `{valor}` e não `{double}`?** O `{double}` segue o idioma do `.feature` e, em português, esperaria `7,99`. O `{valor}` aceita o valor exatamente como aparece na tela.
- **Duas anotações com o mesmo texto quebram o Cucumber** (passo ambíguo). Por isso o checkout usa `devo ver no checkout a mensagem de erro…`, diferente do login.

---

## Tags

No `.feature`, as tags são escritas com `@`. O motor do Cucumber transforma cada uma numa **tag do JUnit**, sem o `@`, e é por ela que os cenários são filtrados.

| Tag | O que marca |
|---|---|
| `@regression` | Todo cenário que **deve passar**. É o que roda na CI a cada push |
| `@expected-failure` | Cenários que **devem falhar**: encontram os defeitos de propósito dos usuários especiais. Nunca recebem `@regression` |
| `@login` · `@login-valido` · `@login-invalido` | Login |
| `@cart` · `@cart-adicionar` · `@cart-remover` · `@cart-persistencia` | Carrinho |
| `@checkout` · `@checkout-finalizar` · `@checkout-cancelar` · `@checkout-resumo` · `@checkout-recibo` | Checkout |
| `@inventory` · `@inventory-ordenacao` · `@inventory-detalhes` | Produtos |
| `@navigation` · `@navigation-menu` · `@navigation-rodape` | Menu lateral e rodapé |
| `@dynamic-catalog` · `@dynamic-spinner` · `@dynamic-lazy-load` · `@dynamic-slider` | Páginas dinâmicas |

As tags **somam de cima para baixo**: Funcionalidade → Esquema → Exemplos. Nos produtos, `@regression` e `@expected-failure` ficam em **blocos de `Exemplos` separados** dentro do mesmo esquema:

```gherkin
  @regression
  Exemplos: Usuários sem defeito na ordenação por preço
    | id    | usuario  |
    | ORD11 | STANDARD |

  @expected-failure
  Exemplos: Usuários com defeito na ordenação (falha esperada)
    | id    | usuario |
    | ORD13 | PROBLEM |
```

---

## Configuração

1. Copie `src/test/resources/config.properties.example` para `config.properties` na mesma pasta.
2. Se quiser, troque o navegador (`browser`), o modo sem janela (`headless`) ou a pasta de downloads (`download.dir`).

Qualquer chave pode ser trocada numa execução, sem editar o arquivo:

```bash
mvn test -Dbrowser=firefox -Dheadless=true
```

---

## Como executar

O filtro usa as **tags do JUnit** (`-Dgroups`), **sem o `@`**. Operadores: `&` (e), `|` (ou), `!` (não) e parênteses. No PowerShell e no cmd, mantenha as aspas em volta do `-Dgroups=...`.

| Comando | O que roda | Resultado esperado |
|---|---|---|
| `mvn test "-Dgroups=regression"` | Regressão completa | **92** cenários, tudo passa |
| `mvn test "-Dgroups=expected-failure"` | Só os defeitos conhecidos | **18** cenários, **todos falham** (é o esperado) |
| `mvn test "-Dgroups=login"` | Login | 12, tudo passa |
| `mvn test "-Dgroups=login & !login-invalido"` | Só os logins válidos | 5, tudo passa |
| `mvn test "-Dgroups=cart"` | Carrinho | 9, tudo passa |
| `mvn test "-Dgroups=checkout"` | Checkout, cálculo do resumo e recibo em PDF | 18, tudo passa |
| `mvn test "-Dgroups=inventory"` | Ordenação e detalhes, com os 5 usuários | 48, **18 falhas esperadas** |
| `mvn test "-Dgroups=inventory & !expected-failure"` | Produtos, sem os defeitos conhecidos | 30, tudo passa |
| `mvn test "-Dgroups=navigation"` | Menu lateral e rodapé | 8, tudo passa |
| `mvn test "-Dgroups=dynamic-catalog"` | Spinner, Lazy Load e Slider | 15, tudo passa |
| `mvn test "-Dgroups=cart \| navigation"` | Combinação com "ou" | 17, tudo passa |
| `mvn test` | Todos os 110 cenários | 18 falhas esperadas |

Se um filtro não encontrar nenhum cenário (uma tag digitada errada, por exemplo), o `@Suite` **falha a execução** em vez de terminar com 0 testes.

**Dica:** escreva a frase nova no `.feature` e rode. O cenário fica **UNDEFINED** e o Cucumber sugere o esqueleto do método que falta, já com a anotação.

### Saídas de cada execução

- `reports/ExtentReport_<data-hora>.html`: um registro por cenário, com as tags como categorias e o print das falhas.
- `reports/cucumber-report.html`: o relatório do Cucumber, passo a passo, com a mensagem do erro e o print embutido nas falhas.
- `target/surefire-reports/TEST-*.xml`: o resultado no formato JUnit, lido pelo Jenkins e pelo resumo do e-mail no GitHub Actions.
- `screenshots/SUCCESS|FAILURE|SKIP/<feature>_<ID>_<data-hora>.png`, por exemplo `detalhes_PD07_...png`.
- `downloads/swag-labs-order-<data-hora>.pdf`: recibo baixado nos cenários de PDF.
- `logs/selenium-framework.log`.

---

## Usuários do Sauce Demo

Todos usam a senha `secret_sauce` e estão no enum `User`. Nos cenários, o usuário aparece pela chave: `Dado que estou logado com o usuário PROBLEM`.

| Chave | Usuário | Comportamento |
|---|---|---|
| `STANDARD` | `standard_user` | sem defeitos |
| `LOCKED_OUT` | `locked_out_user` | bloqueado: não consegue fazer login |
| `PROBLEM` | `problem_user` | ordenação não funciona; abre o produto errado; falhas no carrinho |
| `PERFORMANCE_GLITCH` | `performance_glitch_user` | funciona, mas com ~5 segundos de lentidão |
| `ERROR` | `error_user` | ordenação abre um alerta; erros no carrinho |
| `VISUAL` | `visual_user` | preços exibidos aleatórios na lista |

---

## Técnicas de teste aplicadas

- **Particionamento de Equivalência (usuários):** cada perfil com comportamento diferente é uma partição. O `standard_user` testa os 6 produtos.
- **Análise de Valor Limite (produtos):** os outros usuários testam só o **primeiro** (id 0) e o **último** (id 5) produto, os limites da faixa de ids. Resultado: 8 combinações em vez de 24, detectando os mesmos defeitos.
- **Valor Limite no cálculo do resumo:** o item mais barato, o mais caro (a taxa arredonda para cima), 2 e 3 itens e todos os 6.
- **Valor Limite no Lazy Load:** início da lista, fim do 1º lote, início do 2º lote, último item e o primeiro depois dele (a lista recomeça).

**Risco aceito:** um defeito que aparecesse só num produto do meio da faixa, para um usuário especial, não seria detectado.

---

## Páginas dinâmicas e recibo em PDF

| Situação | Como os passos esperam |
|---|---|
| Spinner com tempo **aleatório** (0,5 a 3 s) | Esperam o spinner sumir (`waitForProductsToLoad`). Um tempo fixo falharia sempre que demorasse mais |
| Lazy Load: card mostra `Loading…` até aparecer na tela | Esperam o card carregar (`waitForCardToLoad`). A quantidade inicial depende da janela, então os cenários dizem "pelo menos 6" |
| Slider troca sozinho a cada 2 s | Esperam a troca (`waitForSlideToChange`) e confirmam a leitura do slide |
| Recibo em PDF | A pasta `downloads/` é limpa antes do clique; `DownloadUtils.waitForFile` espera o arquivo; `PdfUtils.readText` extrai o texto |
| Troca de página (ex.: abrir o carrinho) | A ação espera a URL nova (`cart.html`) antes de devolver o Page Object. Sem isso, a contagem de itens lia a lista de produtos, que usa o mesmo `data-test` |

O texto `Loading…` usa o caractere de reticências (`…`), e não três pontos.

No recibo, o **nome do arquivo** usa o horário **UTC** do pedido, e o **conteúdo** usa o horário **local**. Os passos comparam cada um com o fuso certo, com uma pequena tolerância, e funcionam tanto no Jenkins (Windows, horário de Brasília) quanto no GitHub Actions (Linux, UTC).

---

## Integração contínua (CI)

| | GitHub Actions (`.github/workflows/ci.yml`) | Jenkins (`Jenkinsfile`) |
|---|---|---|
| Quando roda | A cada push e pull request na `master`, ou manualmente em *Actions → Run workflow* | Verifica o repositório a cada ~5 minutos, ou em *Construir com parâmetros* |
| O que roda | Input `tags`, sintaxe do JUnit (padrão `regression`) | Parâmetro `TAGS`, sintaxe do JUnit (padrão `regression`) |
| Execução | `mvn test "-Dgroups=..."`, headless, no Linux | `mvn test "-Dgroups=..."`, headless, no Windows |
| Resultados | Relatórios, prints, logs e `TEST-*.xml` como *artifacts*; totais no log do passo "Resumir resultados" | Plugin **JUnit** (aba *Resultado de testes*, com tendência entre builds) e *Artefatos* |
| E-mail | Action `dawidd6/action-send-mail`, com os segredos `EMAIL_SENDER`, `EMAIL_APP_PASSWORD` e `EMAIL_RECIPIENTS` | Plugin **Email Extension** (`emailext`), com o SMTP configurado no Jenkins (credencial `gmail-smtp`) e a credencial `email-recipients` |

No GitHub Actions, os totais do e-mail são somados dos `TEST-*.xml`. O JUnit não informa "passou": a conta é `tests − failures − errors − skipped`.

**O e-mail é responsabilidade da esteira, não do framework:** o código não tem nada de e-mail, a senha nunca chega ao Maven e rodar localmente não dispara mensagens. O e-mail traz o status (PASSOU/FALHOU), os totais e os dois relatórios em anexo.

O `config.properties` real não vai para o Git. Na CI, ele é criado a partir do `config.properties.example`.

---

## Do TestNG para o JUnit 5

O que mudou em relação ao [selenium-cucumber-testng-2026](https://github.com/ramosth/selenium-cucumber-testng-2026), e por quê.

| Peça | Cucumber + TestNG | Cucumber + JUnit 5 |
|---|---|---|
| Dependências | `testng` + `cucumber-testng` | `junit-bom` + `junit-platform-suite` + `cucumber-junit-platform-engine` + `junit-jupiter-api` |
| Runner | `extends AbstractTestNGCucumberTests` + `@CucumberOptions` | Classe vazia com `@Suite`, `@IncludeEngines("cucumber")`, `@SelectPackages("features")` |
| Configuração | Atributos do `@CucumberOptions` | `junit-platform.properties` |
| Filtro de tags | `-Dcucumber.filter.tags="@login and not @login-invalido"` | `-Dgroups="login & !login-invalido"` |
| Verificações | `Assert.assertEquals(atual, esperado, msg)` | `Assertions.assertEquals(esperado, atual, msg)` |
| Comparar sem ordem | `Assert.assertEqualsNoOrder` | Não existe: as duas listas são ordenadas antes do `assertEquals` |
| Várias verificações juntas | Para na primeira falha | `assertAll` mostra todas as falhas de uma vez |
| Relatório do Surefire | `testng-results.xml` | `TEST-*.xml` (formato JUnit) |
| Jenkins | Plugin TestNG Results | Plugin JUnit |

**O que a migração ensinou:**

- **A ordem do `assertEquals` inverte.** Trocada, a linha compila e o teste passa ou falha do mesmo jeito, mas a mensagem diz o contrário da realidade (`expected` é o que a tela mostrou). A regra para lembrar: "espero X e recebi Y".
- **`-Dcucumber.filter.tags` só pula os cenários.** No JUnit, o Cucumber entrega todos os cenários à plataforma e pula os que não batem: `@login` virava *110 executados, 98 ignorados*, poluindo o e-mail e o Jenkins e escondendo um ignorado de verdade. Com `-Dgroups`, o filtro é do JUnit e os cenários fora dele nem entram no plano.
- **No `-Dgroups` a tag vai sem `@`.** `-Dgroups=@regression` não é uma expressão válida do JUnit e a execução falha antes de rodar qualquer cenário.
- **O Surefire escolhe um "provider", e o TestNG ganha.** Durante a migração, com o TestNG e o JUnit no projeto ao mesmo tempo, o Surefire escolheria o TestNG e rodaria 0 testes sem erro. O `surefire-junit-platform` foi forçado no plugin até o TestNG sair do pom.
- **Migração incremental, com o compilador como prova final.** O runner mudou primeiro, os steps foram migrados uma classe por vez (validando a tag de cada uma) e só no fim o TestNG saiu do pom: se sobrasse algum `org.testng`, o projeto não compilaria.
- **Nomes legíveis nos relatórios.** A estratégia `short` + `pickle` faz o Jenkins mostrar `CT01 - Login com usuário padrão` em vez de `Example #1.1`.
- **Os testes rodam em processo separado.** O `forkCount 0` herdado foi removido; os `-D` da linha de comando continuam chegando aos testes (conferido com `-Dheadless=true`).
- **Um teste instável apareceu e foi corrigido nos três repositórios.** O MNU05 falhou uma vez na CI (`expected [0] but found [6]`): o `openCart()` devolvia a página do carrinho antes de ela abrir, e a contagem lia os 6 produtos da lista. Agora a ação espera `cart.html`.

---

## VS Code

Extensão recomendada: **Cucumber (Gherkin) Full Support** (Alexander Krechik). O `.vscode/settings.json` já ensina a ela:

- os passos em português (`gherkinDefinitionPart` com `@Dado`, `@Quando`, `@Entao`, `@E`, `@Mas`);
- os tipos personalizados (`customParameters`), aceitando também as lacunas dos esquemas (`<usuario>`);
- o recuo das palavras-chave em português no formatador (Shift+Alt+F).

Com isso, Ctrl+clique numa frase leva ao método Java, e frases sem passo ficam sublinhadas.

---

## Cobertura

| Funcionalidade | Cenários | O que é verificado |
|---|---|---|
| Login | CT01–CT12 | 5 perfis entrando no sistema; bloqueado, campos em branco, senha errada, usuário inexistente, maiúsculas |
| Carrinho | CAR01–03, RMV01–03, PER01–03 | Adicionar 1, 2 e todos; remover pela lista e pelo carrinho; carrinho mantido ao navegar, recarregar e após logout/login |
| Checkout | CHK01–05, CNL01–03 | Pedido concluído; campos obrigatórios; Cancel em cada passo e Continue Shopping |
| Resumo do pedido | CALC01–05 | Item total, taxa de 8% e total = item total + taxa |
| Recibo em PDF | PDF01–05 | Download, nome do arquivo (UTC), textos, comprador, itens, valores e data (local) |
| Ordenação | ORD01–ORD20 | Ordem padrão e as 3 ordenações, com os 5 usuários (8 falhas esperadas) |
| Detalhes do produto | PD01–PD14, ADR01–ADR14 | Preço na lista, id na URL, nome, descrição, preço, imagem; adicionar e remover pelos detalhes (10 falhas esperadas) |
| Menu lateral | MNU01–05 | Abrir/fechar, All Items, About na mesma aba, Logout e bloqueio sem login, Reset App State |
| Rodapé | FTR01–03 | Redes sociais (endereço e nova aba), copyright com o ano atual, rodapé no carrinho |
| Dynamic Catalog | SPN01–04, LZY01–07, SLD01–04 | Spinner, lista infinita e carregamento sob demanda, slider automático e por clique |

---

## Como adicionar um novo requisito

1. Escreva a **user story** e os cenários no `.feature` da funcionalidade (ou crie um novo em `features/<módulo>/`), com o ID no nome e as tags do módulo.
2. Rode só aquela tag (`mvn test "-Dgroups=<tag>"`). As frases novas aparecem como **UNDEFINED**, com o esqueleto do método.
3. Implemente os passos no `*Steps` do módulo, usando as páginas de `src/main` (se faltar uma ação ou pergunta na página, ela entra lá, sem Assert).
4. Rode localmente até passar.
5. Commit e push, e valide manualmente nas duas esteiras com a tag do módulo.
6. Com as esteiras verdes, adicione `@regression` (ou `@expected-failure`, se o cenário expõe um defeito conhecido).

---

## Defeitos encontrados (ainda sem teste)

- **Reset App State não atualiza os botões da lista de produtos** (inclusive com o `standard_user`): o carrinho é esvaziado, mas os produtos continuam mostrando "Remove" até a página ser recarregada.
- **About com o `problem_user`** aponta para `https://saucelabs.com/error/404`, em vez do site da Sauce Labs.
- **Item repetido no Lazy Load**: as posições 13 e 14 da lista mostram o mesmo produto, `Test.allTheThings() T-Shirt (Red) (L)`. Os outros produtos aparecem uma vez em cada tamanho.
- **Carrinho aparece vazio ao clicar no ícone do carrinho estando na própria página do carrinho** (inclusive com o `standard_user`): depois de remover um item na página do carrinho, um novo clique no ícone exibe a lista vazia, embora o contador continue mostrando os itens restantes. Os passos do carrinho só clicam no ícone quando não estão no carrinho (`CartSteps.carrinho()`).

### Dúvida de requisito

- **O carrinho continua após o logout**, porque fica guardado no navegador e não na conta do usuário. Se outra pessoa entrar com **outro usuário** no mesmo navegador, verá o carrinho de quem saiu. O cenário PER03 protege o comportamento atual, mas vale confirmar com o time se o carrinho deveria ser por usuário.

---

## Próximos passos

- Seguem no roteiro: Playwright + Java, Cypress, Appium e Robot Framework.

---

## Portfólio

| Projeto | Foco |
|---|---|
| [selenium-automation-framework-2026](https://github.com/ramosth/selenium-automation-framework-2026) | Selenium + TestNG (UI) |
| [selenium-cucumber-testng-2026](https://github.com/ramosth/selenium-cucumber-testng-2026) | Selenium + Cucumber + TestNG (UI) |
| **selenium-cucumber-junit-2026** | **Selenium + Cucumber + JUnit 5 (UI)** |
| [restassured-cucumber-junit-2026](https://github.com/ramosth/restassured-cucumber-junit-2026) | REST Assured + Cucumber + JUnit 5 (API) |

---

Thamires Ramos · QA Analyst

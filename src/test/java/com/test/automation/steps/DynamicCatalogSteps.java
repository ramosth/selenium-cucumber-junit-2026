package com.test.automation.steps;

import java.util.List;

import org.testng.Assert;

import com.test.automation.context.TestContext;
import com.test.automation.data.Product;
import com.test.automation.pages.LazyLoadPage;
import com.test.automation.pages.SliderPage;
import com.test.automation.pages.SpinnerPage;
import com.test.automation.pages.components.HeaderComponent;

import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

/**
 * Passos das páginas dinâmicas do menu Dynamic Catalog: Spinner, Lazy Load e Slider.
 *
 * Todas as esperas são explícitas (métodos waitFor... das páginas), nunca Thread.sleep:
 * o tempo de carregamento varia, e um tempo fixo falharia sempre que demorasse mais.
 */
public class DynamicCatalogSteps {

    // Margem para comparar números com casas decimais (29.99 pode virar 29.989999... no computador)
    private static final double PRICE_DELTA = 0.001;
    private static final int TOTAL_PRODUTOS = Product.values().length;

    private final TestContext context;

    // Informações guardadas entre os passos do mesmo cenário
    private int cardsAntesDaRolagem;   // Lazy Load: quantidade de cards antes de rolar até o fim
    private int posicaoDoCard;         // Lazy Load: card que ficou logo abaixo da tela
    private int pontoAntesDaTroca;     // Slider: ponto ativo antes da troca automática

    public DynamicCatalogSteps(TestContext context) {
        this.context = context;
    }

    private SpinnerPage spinner() {
        return new SpinnerPage(context.getDriver());
    }

    private LazyLoadPage lazyLoad() {
        return new LazyLoadPage(context.getDriver());
    }

    private SliderPage slider() {
        return new SliderPage(context.getDriver());
    }

    // ---------- Abrir as páginas pelo menu ----------

    @Quando("abro o Spinner pelo menu Dynamic Catalog")
    public void abroOSpinner() {
        SpinnerPage page = new HeaderComponent(context.getDriver()).goToSpinner();
        Assert.assertTrue(page.isLoaded(), "A página do Spinner não foi exibida.");
    }

    @Quando("abro o Lazy Load pelo menu Dynamic Catalog")
    public void abroOLazyLoad() {
        LazyLoadPage page = new HeaderComponent(context.getDriver()).goToLazyLoad();
        Assert.assertTrue(page.isLoaded(), "A página do Lazy Load não foi exibida.");
    }

    @Quando("abro o Slider pelo menu Dynamic Catalog")
    public void abroOSlider() {
        SliderPage page = new HeaderComponent(context.getDriver()).goToSlider();
        Assert.assertTrue(page.isLoaded(), "A página do Slider não foi exibida.");
    }

    // ---------- Spinner ----------

    @Quando("espero os produtos do Spinner carregarem")
    public void esperoOsProdutosDoSpinnerCarregarem() {
        Assert.assertTrue(spinner().waitForProductsToLoad(), "O spinner não sumiu ou os produtos não apareceram.");
    }

    @Quando("recarrego a página do Spinner")
    public void recarregoAPaginaDoSpinner() {
        spinner().refreshPage();
    }

    @Entao("o spinner deve aparecer enquanto os produtos carregam")
    public void oSpinnerDeveAparecer() {
        Assert.assertTrue(spinner().waitForSpinnerToAppear(), "O spinner de carregamento não apareceu.");
    }

    @Entao("o spinner deve sumir quando os produtos aparecerem")
    public void oSpinnerDeveSumir() {
        SpinnerPage page = spinner();
        Assert.assertTrue(page.waitForProductsToLoad(), "O spinner não sumiu ou os produtos não apareceram.");
        Assert.assertFalse(page.isSpinnerDisplayed(), "O spinner continua na tela depois do carregamento.");
    }

    @Entao("a grade do Spinner deve mostrar {int} produtos")
    public void aGradeDoSpinnerDeveMostrar(int quantidadeEsperada) {
        Assert.assertEquals(spinner().getProductCount(), quantidadeEsperada,
                "Quantidade de produtos na grade diferente da esperada.");
    }

    // O id do produto (Product.getId) é o que identifica cada card da grade
    @Entao("a grade do Spinner deve mostrar o nome e o preço do cadastro dos produtos:")
    public void aGradeDoSpinnerDeveMostrarNomeEPreco(List<Product> produtos) {
        SpinnerPage page = spinner();
        for (Product produto : produtos) {
            Assert.assertEquals(page.getProductName(produto.getId()), produto.getName(),
                    "Nome errado no card de id " + produto.getId());
            Assert.assertEquals(page.getProductPrice(produto.getId()), produto.getPrice(), PRICE_DELTA,
                    "Preço errado para " + produto.getName());
        }
    }

    // ---------- Lazy Load: lista infinita ----------

    @Entao("a lista deve começar com pelo menos {int} cards")
    public void aListaDeveComecarComPeloMenos(int minimo) {
        int quantidade = lazyLoad().getCardCount();
        Assert.assertTrue(quantidade >= minimo,
                "A página deveria abrir com pelo menos " + minimo + " cards, mas abriu com " + quantidade + ".");
    }

    @Quando("rolo até o fim da lista")
    public void roloAteOFimDaLista() {
        LazyLoadPage page = lazyLoad();
        cardsAntesDaRolagem = page.getCardCount();
        Assert.assertTrue(page.loadMore(), "Nenhum card novo foi carregado ao rolar até o fim.");
    }

    @Entao("devem entrar pelo menos {int} cards novos")
    public void devemEntrarPeloMenosCardsNovos(int minimo) {
        int depois = lazyLoad().getCardCount();
        context.getExtentTest().info("Cards antes: " + cardsAntesDaRolagem + " | depois: " + depois);
        Assert.assertTrue(depois >= cardsAntesDaRolagem + minimo,
                "Deveriam entrar pelo menos " + minimo + " cards. Antes: " + cardsAntesDaRolagem + ", depois: " + depois);
    }

    @Entao("o total de cards deve ser múltiplo de {int}")
    public void oTotalDeCardsDeveSerMultiploDe(int lote) {
        int total = lazyLoad().getCardCount();
        Assert.assertEquals(total % lote, 0, "Os cards deveriam chegar em lotes de " + lote + ". Total: " + total);
    }

    // ---------- Lazy Load: card só carrega quando aparece ----------

    @Quando("deixo um card logo abaixo da área visível da tela")
    public void deixoUmCardAbaixoDaTela() {
        posicaoDoCard = lazyLoad().loadUntilLastCardIsBelowScreen();
        Assert.assertTrue(posicaoDoCard >= 0, "Não foi possível deixar um card abaixo da tela para o teste.");
        context.getExtentTest().info("Card escolhido (abaixo da tela): posição " + posicaoDoCard);
    }

    @Entao("esse card deve mostrar o texto {string}")
    public void esseCardDeveMostrarOTexto(String textoEsperado) {
        LazyLoadPage page = lazyLoad();
        Assert.assertTrue(page.isPlaceholderShown(posicaoDoCard),
                "O card " + posicaoDoCard + " está fora da tela, mas não mostra o placeholder.");
        Assert.assertEquals(page.getPlaceholderText(posicaoDoCard), textoEsperado, "Texto do placeholder diferente.");
    }

    @Quando("rolo a tela até esse card")
    public void roloATelaAteEsseCard() {
        lazyLoad().scrollToCard(posicaoDoCard);
    }

    @Entao("esse card deve carregar o produto")
    public void esseCardDeveCarregarOProduto() {
        LazyLoadPage page = lazyLoad();
        Assert.assertTrue(page.waitForCardToLoad(posicaoDoCard),
                "O card " + posicaoDoCard + " não carregou ao aparecer na tela.");
        Assert.assertFalse(page.isPlaceholderShown(posicaoDoCard),
                "O card " + posicaoDoCard + " carregou, mas o placeholder continua.");
    }

    // ---------- Lazy Load: conteúdo dos cards ----------

    @Quando("rolo a lista até o card da posição {int}")
    public void roloAListaAteOCardDaPosicao(int posicao) {
        LazyLoadPage page = lazyLoad();
        Assert.assertTrue(page.loadUntilCardExists(posicao),
                "O card " + posicao + " não foi criado mesmo rolando a lista.");
        page.scrollToCard(posicao);
        Assert.assertTrue(page.waitForCardToLoad(posicao), "O card " + posicao + " não carregou.");
    }

    @Entao("o card da posição {int} deve mostrar o produto {string} por {valor}")
    public void oCardDaPosicaoDeveMostrar(int posicao, String nomeEsperado, Double precoEsperado) {
        LazyLoadPage page = lazyLoad();
        Assert.assertEquals(page.getCardName(posicao), nomeEsperado, "Nome errado no card " + posicao);
        Assert.assertEquals(page.getCardPrice(posicao), precoEsperado, PRICE_DELTA, "Preço errado no card " + posicao);
    }

    // ---------- Slider ----------

    @Entao("o slider deve ter {int} pontos")
    public void oSliderDeveTerPontos(int quantidadeEsperada) {
        Assert.assertEquals(slider().getDotCount(), quantidadeEsperada, "Quantidade de pontos diferente da esperada.");
    }

    @Entao("apenas {int} ponto deve estar ativo")
    public void apenasUmPontoDeveEstarAtivo(int quantidadeEsperada) {
        Assert.assertEquals(slider().getActiveDotCount(), quantidadeEsperada,
                "Quantidade de pontos ativos diferente da esperada.");
    }

    @Quando("espero o slider trocar de produto sozinho")
    public void esperoOSliderTrocarSozinho() {
        SliderPage page = slider();
        pontoAntesDaTroca = page.getActiveDotIndex();
        Assert.assertTrue(page.waitForSlideToChange(pontoAntesDaTroca), "O slider não trocou de produto sozinho.");
    }

    @Entao("o slider deve estar no ponto seguinte")
    public void oSliderDeveEstarNoPontoSeguinte() {
        // % (resto da divisão) faz a conta "dar a volta": depois do 5 vem o 0
        int esperado = (pontoAntesDaTroca + 1) % TOTAL_PRODUTOS;
        Assert.assertEquals(slider().getActiveDotIndex(), esperado,
                "O slider não foi do ponto " + pontoAntesDaTroca + " para o " + esperado + ".");
    }

    // A posição do ponto é o id do produto (Product.getId)
    @Quando("clico no ponto do produto {produto}")
    public void clicoNoPontoDoProduto(Product produto) {
        SliderPage.Slide slide = slider().showSlide(produto.getId());
        Assert.assertEquals(slide.index(), produto.getId(), "O ponto clicado não ficou ativo: " + produto.getName());
    }

    @Entao("o slider deve mostrar o produto {produto}")
    public void oSliderDeveMostrarOProduto(Product produto) {
        SliderPage.Slide slide = slider().readSlide();
        Assert.assertEquals(slide.index(), produto.getId(), "O slider está em outro ponto.");
        Assert.assertEquals(slide.name(), produto.getName(), "Nome errado no slider.");
        Assert.assertEquals(slide.price(), produto.getPrice(), PRICE_DELTA, "Preço errado no slider.");
    }

    @Entao("cada ponto do slider deve mostrar o produto do cadastro:")
    public void cadaPontoDoSliderDeveMostrarOProduto(List<Product> produtos) {
        SliderPage page = slider();
        for (Product produto : produtos) {
            SliderPage.Slide slide = page.showSlide(produto.getId());
            Assert.assertEquals(slide.index(), produto.getId(), "O ponto clicado não ficou ativo: " + produto.getName());
            Assert.assertEquals(slide.name(), produto.getName(), "Nome errado no ponto " + produto.getId());
            Assert.assertEquals(slide.price(), produto.getPrice(), PRICE_DELTA, "Preço errado para " + produto.getName());
        }
    }
}
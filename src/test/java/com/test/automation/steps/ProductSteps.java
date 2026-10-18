package com.test.automation.steps;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.testng.Assert;

import com.test.automation.context.TestContext;
import com.test.automation.data.Product;
import com.test.automation.pages.InventoryPage;
import com.test.automation.pages.ProductDetailsPage;

import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

/**
 * Passos da lista de produtos (ordenação) e da página de detalhes.
 *
 * Ordenação: depois de ordenar na tela, compara a lista exibida com uma cópia dela
 * ordenada pelo próprio Java. Se forem iguais, a tela está na ordem certa.
 *
 * Detalhes: os valores esperados (nome, id, preço e descrição) vêm do enum Product.
 *
 * Os passos verificam o comportamento CORRETO. Com os usuários que têm defeitos de
 * propósito, alguns cenários falham: eles têm a tag @expected-failure.
 */
public class ProductSteps {

    // Margem para comparar números com casas decimais (29.99 pode virar 29.989999... no computador)
    private static final double PRICE_DELTA = 0.001;

    private final TestContext context;

    // Texto do alerta que apareceu ao ordenar (null = não apareceu). Guardado entre os passos do cenário.
    private String alertaDaOrdenacao;

    public ProductSteps(TestContext context) {
        this.context = context;
    }

    // ---------- Ordenação ----------

    @Quando("ordeno os produtos por {string}")
    public void ordenoOsProdutosPor(String opcao) {
        InventoryPage inventoryPage = new InventoryPage(context.getDriver());
        inventoryPage.sortBy(opcao);
        // O error_user abre um alerta ao ordenar: ele é fechado aqui e o texto guardado para a validação
        alertaDaOrdenacao = inventoryPage.acceptAlertIfPresent();
    }

    @Entao("não deve aparecer alerta de erro")
    public void naoDeveAparecerAlertaDeErro() {
        Assert.assertNull(alertaDaOrdenacao, "Apareceu um alerta de erro ao ordenar: " + alertaDaOrdenacao);
    }

    @Entao("a ordenação selecionada deve ser {string}")
    public void aOrdenacaoSelecionadaDeveSer(String opcaoEsperada) {
        Assert.assertEquals(new InventoryPage(context.getDriver()).getSelectedSort(), opcaoEsperada,
                "A opção de ordenação selecionada é diferente da esperada.");
    }

    @Entao("os produtos devem estar em ordem alfabética de A a Z")
    public void osProdutosDevemEstarDeAaZ() {
        List<String> nomes = new InventoryPage(context.getDriver()).getProductNames();
        List<String> esperado = new ArrayList<>(nomes);
        Collections.sort(esperado);
        Assert.assertEquals(nomes, esperado, "Os produtos não estão em ordem alfabética (A a Z).");
    }

    @Entao("os produtos devem estar em ordem alfabética de Z a A")
    public void osProdutosDevemEstarDeZaA() {
        List<String> nomes = new InventoryPage(context.getDriver()).getProductNames();
        List<String> esperado = new ArrayList<>(nomes);
        esperado.sort(Comparator.reverseOrder());
        Assert.assertEquals(nomes, esperado, "Os produtos não estão em ordem alfabética decrescente (Z a A).");
    }

    @Entao("os preços devem estar do menor para o maior")
    public void osPrecosDevemEstarDoMenorParaOMaior() {
        List<Double> precos = new InventoryPage(context.getDriver()).getProductPrices();
        context.getExtentTest().info("Preços exibidos: " + precos);
        List<Double> esperado = new ArrayList<>(precos);
        Collections.sort(esperado);
        Assert.assertEquals(precos, esperado, "Os preços não estão em ordem crescente.");
    }

    @Entao("os preços devem estar do maior para o menor")
    public void osPrecosDevemEstarDoMaiorParaOMenor() {
        List<Double> precos = new InventoryPage(context.getDriver()).getProductPrices();
        context.getExtentTest().info("Preços exibidos: " + precos);
        List<Double> esperado = new ArrayList<>(precos);
        esperado.sort(Comparator.reverseOrder());
        Assert.assertEquals(precos, esperado, "Os preços não estão em ordem decrescente.");
    }

    // ---------- Detalhes do produto ----------

    @Entao("na lista de produtos, o produto {produto} deve mostrar o preço do cadastro")
    public void naListaOProdutoDeveMostrarOPrecoDoCadastro(Product produto) {
        Assert.assertEquals(new InventoryPage(context.getDriver()).getProductPrice(produto.getName()),
                produto.getPrice(), PRICE_DELTA, "Preço na lista de produtos diferente do esperado.");
    }

    @Entao("a página de detalhes deve ser do produto {produto}")
    public void aPaginaDeDetalhesDeveSerDoProduto(Product produto) {
        Assert.assertEquals(new ProductDetailsPage(context.getDriver()).getProductIdFromUrl(), produto.getId(),
                "A página aberta é de outro produto (id na URL).");
    }

    @Entao("a página de detalhes deve mostrar o nome, a descrição, o preço e a imagem do produto {produto}")
    public void aPaginaDeDetalhesDeveMostrarOsDadosDoProduto(Product produto) {
        ProductDetailsPage detailsPage = new ProductDetailsPage(context.getDriver());
        Assert.assertEquals(detailsPage.getProductName(), produto.getName(), "Nome diferente do esperado.");
        Assert.assertEquals(detailsPage.getProductDescription(), produto.getDescription(),
                "Descrição diferente da esperada.");
        Assert.assertEquals(detailsPage.getProductPrice(), produto.getPrice(), PRICE_DELTA,
                "Preço na página de detalhes diferente do esperado.");
        Assert.assertTrue(detailsPage.isProductImageVisible(), "A imagem do produto não está visível.");
    }

    // ---------- Carrinho pela página de detalhes ----------

    @Quando("adiciono o produto ao carrinho pela página de detalhes")
    public void adicionoOProdutoPelaPaginaDeDetalhes() {
        new ProductDetailsPage(context.getDriver()).addToCart();
    }

    @Quando("removo o produto do carrinho pela página de detalhes")
    public void removoOProdutoPelaPaginaDeDetalhes() {
        new ProductDetailsPage(context.getDriver()).removeFromCart();
    }

    @Entao("o botão Remove deve aparecer na página de detalhes")
    public void oBotaoRemoveDeveAparecer() {
        Assert.assertTrue(new ProductDetailsPage(context.getDriver()).isRemoveButtonVisible(),
                "O botão 'Remove' não apareceu após adicionar.");
    }

    @Entao("o botão Remove não deve aparecer na página de detalhes")
    public void oBotaoRemoveNaoDeveAparecer() {
        Assert.assertFalse(new ProductDetailsPage(context.getDriver()).isRemoveButtonVisible(),
                "O botão 'Remove' continuou aparecendo após remover.");
    }
}
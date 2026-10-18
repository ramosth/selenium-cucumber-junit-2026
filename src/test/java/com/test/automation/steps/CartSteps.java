package com.test.automation.steps;

import java.util.List;

import org.junit.jupiter.api.Assertions;

import com.test.automation.context.TestContext;
import com.test.automation.data.Product;
import com.test.automation.pages.CartPage;
import com.test.automation.pages.InventoryPage;
import com.test.automation.pages.ProductDetailsPage;
import com.test.automation.pages.components.HeaderComponent;
import com.test.automation.utils.Log;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

/**
 * Passos do carrinho: adicionar, remover, navegar e validar.
 *
 * Cada passo cria a página de que precisa a partir do driver, então os passos
 * não dependem uns dos outros. O cabeçalho (contador e ícone do carrinho) existe
 * em todas as páginas, por isso o HeaderComponent é usado direto.
 *
 * Produtos chegam como Product (ver ParameterTypes): {produto} nas frases e
 * List<Product> nas tabelas.
 *
 * Verificações com o Assertions do JUnit 5. Atenção à ordem no assertEquals:
 * primeiro o ESPERADO, depois o valor ATUAL (o contrário do TestNG).
 */
public class CartSteps {

    private final TestContext context;

    public CartSteps(TestContext context) {
        this.context = context;
    }

    /**
     * Página do carrinho: abre pelo ícone só se ainda não estiver nela.
     * Clicar no ícone estando no próprio carrinho, depois de remover um item,
     * faz o Sauce Demo exibir a lista vazia (o contador continua certo).
     */
    private CartPage carrinho() {
        if (context.getDriver().getCurrentUrl().contains("cart.html")) {
            return new CartPage(context.getDriver());
        }
        return new HeaderComponent(context.getDriver()).openCart();
    }

    // ---------- Adicionar ----------

    // Duas frases, uma ação: "Dado que adicionei..." (pré-condição) e "Quando
    // adiciono..." (ação do teste)
    @Dado("que adicionei ao carrinho os produtos:")
    @Quando("adiciono ao carrinho os produtos:")
    public void adicionoAoCarrinhoOsProdutos(List<Product> produtos) {
        InventoryPage inventoryPage = new InventoryPage(context.getDriver());
        for (Product produto : produtos) {
            inventoryPage.addProductToCart(produto.getName());
        }
        Log.info("Produtos adicionados ao carrinho: " + produtos);
    }

    // ---------- Remover ----------

    @Quando("removo o produto {produto} pela lista de produtos")
    public void removoOProdutoPelaListaDeProdutos(Product produto) {
        new InventoryPage(context.getDriver()).removeProductFromCart(produto.getName());
    }

    @Quando("removo o produto {produto} pela página do carrinho")
    public void removoOProdutoPelaPaginaDoCarrinho(Product produto) {
        carrinho().removeProductFromCart(produto.getName());
    }

    // ---------- Navegação ----------

    @Quando("abro o carrinho")
    public void abroOCarrinho() {
        Assertions.assertTrue(carrinho().isLoaded(), "A página do carrinho não foi exibida.");
    }

    @Quando("volto para a lista de produtos pelo botão Continue Shopping")
    public void voltoParaAListaPeloBotaoContinueShopping() {
        InventoryPage inventoryPage = new CartPage(context.getDriver()).continueShopping();
        Assertions.assertTrue(inventoryPage.isLoaded(), "Não voltou para a página de produtos.");
    }

    @Quando("abro os detalhes do produto {produto}")
    public void abroOsDetalhesDoProduto(Product produto) {
        ProductDetailsPage detailsPage = new InventoryPage(context.getDriver()).goToProductDetails(produto.getName());
        Assertions.assertTrue(detailsPage.isLoaded(), "A página de detalhes não foi exibida.");
    }

    @Quando("volto para a lista de produtos pela página de detalhes")
    public void voltoParaAListaPelaPaginaDeDetalhes() {
        InventoryPage inventoryPage = new ProductDetailsPage(context.getDriver()).backToProducts();
        Assertions.assertTrue(inventoryPage.isLoaded(), "Não voltou para a página de produtos.");
    }

    @Quando("recarrego a página")
    public void recarregoAPagina() {
        new InventoryPage(context.getDriver()).refreshPage();
        Assertions.assertTrue(new InventoryPage(context.getDriver()).isLoaded(),
                "A página de produtos não carregou após recarregar.");
    }

    // ---------- Validações ----------

    @Entao("o contador do carrinho deve ser {int}")
    public void oContadorDoCarrinhoDeveSer(int quantidadeEsperada) {
        // JUnit: (esperado, atual, mensagem)
        Assertions.assertEquals(quantidadeEsperada, new HeaderComponent(context.getDriver()).getCartBadgeCount(),
                "Quantidade no ícone do carrinho diferente da esperada.");
    }

    @Entao("o carrinho deve conter os produtos:")
    public void oCarrinhoDeveConterOsProdutos(List<Product> produtosEsperados) {
        CartPage cartPage = carrinho();
        Assertions.assertTrue(cartPage.isLoaded(), "A página do carrinho não foi exibida.");

        // Sem ordem: o que importa é ter exatamente estes produtos.
        // O JUnit não tem assertEqualsNoOrder; ordenando as duas listas,
        // a comparação ignora a ordem, mas não deixa passar item a mais ou a menos.
        List<String> nomesEsperados = produtosEsperados.stream().map(Product::getName).sorted().toList();
        List<String> nomesNoCarrinho = cartPage.getItemNames().stream().sorted().toList();
        Assertions.assertEquals(nomesEsperados, nomesNoCarrinho,
                "Os itens do carrinho são diferentes dos esperados.");
    }

    @Entao("o carrinho deve estar vazio")
    public void oCarrinhoDeveEstarVazio() {
        Assertions.assertEquals(0, carrinho().getItemCount(), "O carrinho deveria estar vazio.");
    }

    // Botão "Remove" na lista de produtos
    @Entao("na lista de produtos, o produto {produto} deve estar marcado como adicionado")
    public void oProdutoDeveEstarMarcadoComoAdicionado(Product produto) {
        Assertions.assertTrue(new InventoryPage(context.getDriver()).isProductInCart(produto.getName()),
                "O produto '" + produto + "' deveria estar no carrinho (botão 'Remove').");
    }

    // Botão "Add to cart" na lista de produtos
    @Entao("na lista de produtos, o produto {produto} deve estar disponível para adicionar")
    public void oProdutoDeveEstarDisponivelParaAdicionar(Product produto) {
        Assertions.assertFalse(new InventoryPage(context.getDriver()).isProductInCart(produto.getName()),
                "O produto '" + produto + "' não deveria estar no carrinho (botão 'Add to cart').");
    }
}
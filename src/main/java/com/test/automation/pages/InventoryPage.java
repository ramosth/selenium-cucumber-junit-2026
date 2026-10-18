package com.test.automation.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.Select;

import com.test.automation.pages.components.FooterComponent;
import com.test.automation.pages.components.HeaderComponent;
import com.test.automation.utils.Log;

/**
 * Página de produtos (inventory.html), exibida logo após o login.
 */
public class InventoryPage extends BasePage {

    public static final String TITLE = "Products";

    // Opções do dropdown de ordenação (texto exatamente como aparece na tela)
    public static final String SORT_NAME_A_TO_Z = "Name (A to Z)";
    public static final String SORT_NAME_Z_TO_A = "Name (Z to A)";
    public static final String SORT_PRICE_LOW_TO_HIGH = "Price (low to high)";
    public static final String SORT_PRICE_HIGH_TO_LOW = "Price (high to low)";

    @FindBy(css = "[data-test='inventory-item-name']")
    private List<WebElement> productNameElements;

    @FindBy(css = "[data-test='inventory-item-price']")
    private List<WebElement> productPriceElements;

    @FindBy(css = "[data-test='product-sort-container']")
    private WebElement sortDropdownElement;

    public InventoryPage(WebDriver driver) {
        super(driver);
    }

    /** Retorna o componente de cabeçalho da página. */
    public HeaderComponent header() {
        return new HeaderComponent(driver);
    }

    /** Retorna o componente de rodapé da página. */
    public FooterComponent footer() {
        return new FooterComponent(driver);
    }

    // ---------- Localizadores dinâmicos (dependem do nome do produto) ----------

    /**
     * XPath do card do produto com o nome informado. Base para os localizadores
     * abaixo.
     */
    private String productCard(String productName) {
        return "//div[@data-test='inventory-item'][.//div[@data-test='inventory-item-name' and normalize-space()='"
                + productName + "']]";
    }

    /** Botão "Add to cart"/"Remove" do card do produto. */
    private By productButton(String productName) {
        return By.xpath(productCard(productName) + "//button");
    }

    /** Preço do card do produto. */
    private By productPrice(String productName) {
        return By.xpath(productCard(productName) + "//div[@data-test='inventory-item-price']");
    }

    /** Nome do produto (é clicável e leva para a página de detalhes). */
    private By productNameLink(String productName) {
        return By.xpath(productCard(productName) + "//div[@data-test='inventory-item-name']");
    }

    // ---------- PERGUNTAS ----------

    public boolean isLoaded() {
        return hasTitle(TITLE);
    }

    /** Nomes dos produtos na ordem em que aparecem na tela. */
    public List<String> getProductNames() {
        return getTexts(productNameElements);
    }

    /** Preços dos produtos na ordem em que aparecem na tela ("$29.99" -> 29.99). */
    public List<Double> getProductPrices() {
        return productPriceElements.stream()
                .map(priceElement -> extractValue(priceElement.getText()))
                .toList();
    }

    /** Preço de um produto específico. */
    public double getProductPrice(String productName) {
        return extractValue(waitVisible(productPrice(productName)).getText());
    }

    /** Opção de ordenação selecionada no momento (ex.: "Name (A to Z)"). */
    public String getSelectedSort() {
        return new Select(sortDropdownElement).getFirstSelectedOption().getText();
    }

    /** true se o produto está no carrinho (o botão do card mostra "Remove"). */
    public boolean isProductInCart(String productName) {
        return waitVisible(productButton(productName)).getText().equalsIgnoreCase("Remove");
    }

    // ---------- AÇÕES ----------

    public void addProductToCart(String productName) {
        Log.info("Adicionando ao carrinho: " + productName);
        click(productButton(productName));
    }

    public void removeProductFromCart(String productName) {
        Log.info("Removendo do carrinho: " + productName);
        click(productButton(productName));
    }

    public ProductDetailsPage goToProductDetails(String productName) {
        Log.info("Acessando detalhes do produto: " + productName);
        click(productNameLink(productName));
        return new ProductDetailsPage(driver);
    }

    /** Ordena a lista usando uma das constantes SORT_... desta classe. */
    public void sortBy(String sortOption) {
        Log.info("Ordenando produtos por: " + sortOption);
        new Select(sortDropdownElement).selectByVisibleText(sortOption);
    }
}
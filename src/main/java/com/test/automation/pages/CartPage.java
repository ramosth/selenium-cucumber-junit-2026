package com.test.automation.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.test.automation.pages.components.FooterComponent;
import com.test.automation.pages.components.HeaderComponent;
import com.test.automation.utils.Log;

/**
 * Página do carrinho (cart.html).
 */
public class CartPage extends BasePage {

    public static final String TITLE = "Your Cart";

    @FindBy(css = "[data-test='inventory-item-name']")
    private List<WebElement> itemNameElements;

    @FindBy(css = "[data-test='inventory-item-price']")
    private List<WebElement> itemPriceElements;

    @FindBy(id = "checkout")
    private WebElement checkoutButtonElement;

    @FindBy(id = "continue-shopping")
    private WebElement continueShoppingButtonElement;

    public CartPage(WebDriver driver) {
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

    /** XPath do item do carrinho com o nome informado. Base para os localizadores abaixo. */
    private String productCard(String productName) {
        return "//div[@data-test='inventory-item'][.//div[@data-test='inventory-item-name' and normalize-space()='"
                + productName + "']]";
    }

    /** Botão "Remove" do item. */
    private By removeButton(String productName) {
        return By.xpath(productCard(productName) + "//button");
    }

    /** Preço do item. */
    private By itemPrice(String productName) {
        return By.xpath(productCard(productName) + "//div[@data-test='inventory-item-price']");
    }

    /** Quantidade do item. */
    private By itemQuantity(String productName) {
        return By.xpath(productCard(productName) + "//div[@data-test='item-quantity']");
    }

    // ---------- PERGUNTAS ----------

    public boolean isLoaded() {
        return hasTitle(TITLE);
    }

    /** Nomes dos itens no carrinho. */
    public List<String> getItemNames() {
        return getTexts(itemNameElements);
    }

    /** Preços dos itens no carrinho ("$29.99" -> 29.99). */
    public List<Double> getItemPrices() {
        return itemPriceElements.stream()
                .map(priceElement -> extractValue(priceElement.getText()))
                .toList();
    }

    /** Quantidade de itens diferentes no carrinho (0 = carrinho vazio). */
    public int getItemCount() {
        return itemNameElements.size();
    }

    /** true se o produto estiver no carrinho. */
    public boolean isProductInCart(String productName) {
        return getItemNames().contains(productName);
    }

    /** Preço de um item específico. */
    public double getItemPrice(String productName) {
        return extractValue(waitVisible(itemPrice(productName)).getText());
    }

    /** Quantidade de um item específico ("1" -> 1). */
    public int getItemQuantity(String productName) {
        return Integer.parseInt(waitVisible(itemQuantity(productName)).getText());
    }

    // ---------- AÇÕES ----------

    /** Remove o item do carrinho (ele some da lista). */
    public void removeProductFromCart(String productName) {
        Log.info("Removendo do carrinho: " + productName);
        click(removeButton(productName));
    }

    public CheckoutInformationPage goToCheckout() {
        Log.info("Clicando em Checkout.");
        click(checkoutButtonElement);
        return new CheckoutInformationPage(driver);
    }

    public InventoryPage continueShopping() {
        Log.info("Voltando para a lista de produtos.");
        click(continueShoppingButtonElement);
        return new InventoryPage(driver);
    }
}
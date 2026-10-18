package com.test.automation.pages;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.test.automation.pages.components.FooterComponent;
import com.test.automation.pages.components.HeaderComponent;

/**
 * Página de detalhes do produto (inventory-item.html).
 */
public class ProductDetailsPage extends BasePage {

    public static final String URL = "inventory-item.html";

    @FindBy(css = "[data-test='inventory-item-name']")
    private WebElement productNameElement;

    @FindBy(css = "[data-test='inventory-item-desc']")
    private WebElement productDescriptionElement;

    @FindBy(css = "[data-test='inventory-item-price']")
    private WebElement productPriceElement;

    @FindBy(className = "inventory_details_img")
    private WebElement productImageElement;

    @FindBy(id = "add-to-cart")
    private WebElement addToCartButtonElement;

    @FindBy(id = "remove")
    private WebElement removeButtonElement;

    @FindBy(id = "back-to-products")
    private WebElement backToProductsButtonElement;

    public ProductDetailsPage(WebDriver driver) {
        super(driver);
    }

    /**
     * Retorna o componente de cabeçalho da página.
     */
    public HeaderComponent header() {
        return new HeaderComponent(driver);
    }

    /**
     * Retorna o componente de rodapé da página.
     */
    public FooterComponent footer() {
        return new FooterComponent(driver);
    }

    /** PERGUNTAS: devolvem valor */

    /**
     * Verificar a url da página e o botão de voltar aos produtos, pois a página não
     * tem o título definido. Espera o botão aparecer (a URL muda antes de a página
     * terminar de ser desenhada); false se não aparecer no tempo limite.
     */
    public boolean isLoaded() {
        try {
            return hasPageUrl(URL) && waitVisible(backToProductsButtonElement).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Obter o nome do produto
     */
    public String getProductName() {
        return getText(productNameElement);
    }

    /**
     * Obter a descrição do produto
     */
    public String getProductDescription() {
        return getText(productDescriptionElement);
    }

    /**
     * Obter o preço do produto
     */
    public double getProductPrice() {
        return extractValue(getText(productPriceElement));
    }

    /** Imagem está visível */
    public boolean isProductImageVisible() {
        return isDisplayed(productImageElement);
    }

    /** Botão "Remove" está visível */
    public boolean isRemoveButtonVisible() {
        return isDisplayed(removeButtonElement);
    }

    /** Id do produto na URL (inventory-item.html?id=4 -> 4). */
    public int getProductIdFromUrl() {
        String url = driver.getCurrentUrl();
        return Integer.parseInt(url.substring(url.indexOf("id=") + 3));
    }

    /** AÇÕES */

    /** Clicar no botão "Add to cart" */
    public void addToCart() {
        click(addToCartButtonElement);
    }

    /** Clicar no botão "Remove" */
    public void removeFromCart() {
        click(removeButtonElement);
    }

    /** Clicar no botão "Back to products" */
    public InventoryPage backToProducts() {
        click(backToProductsButtonElement);
        return new InventoryPage(driver);
    }

}

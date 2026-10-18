package com.test.automation.pages;

import java.util.List;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.test.automation.pages.components.FooterComponent;
import com.test.automation.pages.components.HeaderComponent;
import com.test.automation.utils.Log;

/**
 * Checkout, passo 2: resumo do pedido com valores (checkout-step-two.html).
 */
public class CheckoutOverviewPage extends BasePage {

    public static final String TITLE = "Checkout: Overview";

    @FindBy(css = "[data-test='cart-quantity-label']")
    private WebElement quantityLabelElement;

    @FindBy(css = "[data-test='cart-desc-label']")
    private WebElement descriptionLabelElement;

    @FindBy(css = "[data-test='inventory-item-name']")
    private List<WebElement> itemNameElements;

    @FindBy(css = "[data-test='inventory-item-price']")
    private List<WebElement> itemPriceElements;

    @FindBy(css = "[data-test='payment-info-value']")
    private WebElement paymentInfoValueElement;

    @FindBy(css = "[data-test='shipping-info-value']")
    private WebElement shippingInfoValueElement;

    @FindBy(css = "[data-test='subtotal-label']")
    private WebElement subtotalLabelElement;

    @FindBy(css = "[data-test='tax-label']")
    private WebElement taxLabelElement;

    @FindBy(css = "[data-test='total-label']")
    private WebElement totalLabelElement;

    @FindBy(id = "finish")
    private WebElement finishButtonElement;

    @FindBy(id = "cancel")
    private WebElement cancelButtonElement;

    public CheckoutOverviewPage(WebDriver driver) {
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

    // ---------- PERGUNTAS ----------

    public boolean isLoaded() {
        return hasTitle(TITLE);
    }

    /** Cabeçalho da coluna de quantidade: "QTY". */
    public String getQuantityLabel() {
        return getText(quantityLabelElement);
    }

    /** Cabeçalho da coluna de descrição: "Description". */
    public String getDescriptionLabel() {
        return getText(descriptionLabelElement);
    }

    /** Nomes dos itens do pedido. */
    public List<String> getItemNames() {
        return getTexts(itemNameElements);
    }

    /**
     * Preços dos itens do pedido ("$29.99" -> 29.99). Base para conferir o Item
     * total.
     */
    public List<Double> getItemPrices() {
        return itemPriceElements.stream()
                .map(priceElement -> extractValue(priceElement.getText()))
                .toList();
    }

    /** Forma de pagamento (ex.: "SauceCard #31337"). */
    public String getPaymentInformation() {
        return getText(paymentInfoValueElement);
    }

    /** Forma de entrega (ex.: "Free Pony Express Delivery!"). */
    public String getShippingInformation() {
        return getText(shippingInfoValueElement);
    }

    /** "Item total: $29.99" -> 29.99 */
    public double getItemTotal() {
        return extractValue(getText(subtotalLabelElement));
    }

    /** "Tax: $2.40" -> 2.40 */
    public double getTax() {
        return extractValue(getText(taxLabelElement));
    }

    /** "Total: $32.39" -> 32.39 */
    public double getTotal() {
        return extractValue(getText(totalLabelElement));
    }

    // ---------- AÇÕES ----------

    public CheckoutCompletePage finish() {
        Log.info("Finalizando o pedido.");
        click(finishButtonElement);
        return new CheckoutCompletePage(driver);
    }

    /** Cancela o pedido nesta etapa e volta para a lista de produtos. */
    public InventoryPage cancel() {
        Log.info("Cancelando o pedido.");
        click(cancelButtonElement);
        return new InventoryPage(driver);
    }
}
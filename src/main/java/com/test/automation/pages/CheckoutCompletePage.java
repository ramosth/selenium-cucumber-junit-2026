package com.test.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.test.automation.pages.components.FooterComponent;
import com.test.automation.pages.components.HeaderComponent;
import com.test.automation.utils.Log;

/**
 * Checkout, passo final: confirmação do pedido (checkout-complete.html).
 */
public class CheckoutCompletePage extends BasePage {

    public static final String TITLE = "Checkout: Complete!";

    @FindBy(css = "[data-test='pony-express']")
    private WebElement ponyExpressImageElement;

    @FindBy(css = "[data-test='complete-header']")
    private WebElement completeHeaderElement;

    @FindBy(css = "[data-test='complete-text']")
    private WebElement completeTextElement;

    @FindBy(id = "back-to-products")
    private WebElement backHomeButtonElement;

    // Só existe quando a página é aberta pelo fluxo de compra (Finish).
    // Se a página for recarregada ou aberta pela URL, o botão não é criado; por isso usa By
    private final By generatePdfButton = By.id("generate-pdf-order");

    public CheckoutCompletePage(WebDriver driver) {
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

    /** "Thank you for your order!" */
    public String getConfirmationMessage() {
        return getText(completeHeaderElement);
    }

    /** "Your order has been dispatched, and will arrive just as fast as the pony can get there!" */
    public String getCompleteText() {
        return getText(completeTextElement);
    }

    /** true se a imagem do Pony Express estiver visível. */
    public boolean isPonyExpressImageDisplayed() {
        return isDisplayed(ponyExpressImageElement);
    }

    /** true se o botão "Generate PDF order" existir na página. */
    public boolean isGeneratePdfButtonDisplayed() {
        return !driver.findElements(generatePdfButton).isEmpty();
    }

    // ---------- AÇÕES ----------

    public InventoryPage backHome() {
        Log.info("Voltando para a lista de produtos.");
        click(backHomeButtonElement);
        return new InventoryPage(driver);
    }

    /**
     * Clica em "Generate PDF order", que baixa o recibo do pedido.
     * Esperar o download e ler o PDF é feito pela classe utilitária (etapa do PDF).
     */
    public void clickGeneratePdfOrder() {
        Log.info("Gerando o PDF do pedido.");
        click(generatePdfButton);
    }
}
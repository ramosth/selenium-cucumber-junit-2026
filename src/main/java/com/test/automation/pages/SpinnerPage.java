package com.test.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;

import com.test.automation.pages.components.HeaderComponent;
import com.test.automation.utils.Log;

/**
 * Página Dynamic Catalog > Spinner.
 * Ao abrir, mostra um spinner por um tempo aleatório (0,5 a 3 segundos) e depois
 * troca o spinner pela grade com os 6 produtos.
 */
public class SpinnerPage extends BasePage {

    public static final String TITLE = "Dynamic Catalog - Spinner";
    private static final String URL_PATH = "dynamic-catalog-spinner.html";

    // Spinner e grade se alternam: um é removido quando o outro aparece. Por isso usam By.
    private final By spinner = By.cssSelector("[data-test='dynamic-catalog-spinner']");
    private final By productGrid = By.cssSelector("[data-test='dynamic-catalog-spinner-grid']");
    // Os cards têm a mesma classe nas outras páginas do Dynamic Catalog; por isso o seletor começa pela grade
    private final By productCards = By.cssSelector("[data-test='dynamic-catalog-spinner-grid'] .dynamic_catalog_card");

    public SpinnerPage(WebDriver driver) {
        super(driver);
    }

    public HeaderComponent header() {
        return new HeaderComponent(driver);
    }

    // Localizadores dinâmicos: o data-test de cada card tem o id do produto (o mesmo do enum Product)
    private By productName(int productId) {
        return By.cssSelector("[data-test='spinner-item-" + productId + "-name']");
    }

    private By productPrice(int productId) {
        return By.cssSelector("[data-test='spinner-item-" + productId + "-price']");
    }

    // ---------- PERGUNTAS ----------

    /** true se a URL e o título forem os da página do Spinner (com espera). */
    public boolean isLoaded() {
        return hasPageUrl(URL_PATH) && hasTitle(TITLE);
    }

    /** true se o spinner estiver na tela AGORA (sem espera). Use depois do carregamento. */
    public boolean isSpinnerDisplayed() {
        return !driver.findElements(spinner).isEmpty();
    }

    /** Quantidade de produtos na grade. */
    public int getProductCount() {
        return driver.findElements(productCards).size();
    }

    /** Nome do produto exibido no card com esse id. */
    public String getProductName(int productId) {
        return waitVisible(productName(productId)).getText();
    }

    /** Preço do produto exibido no card com esse id (ex.: "$29.99" vira 29.99). */
    public double getProductPrice(int productId) {
        return extractValue(waitVisible(productPrice(productId)).getText());
    }

    // ---------- ESPERAS ----------

    /** Espera o spinner aparecer; false se ele não aparecer no tempo limite. */
    public boolean waitForSpinnerToAppear() {
        Log.info("Aguardando o spinner de carregamento aparecer.");
        try {
            waitVisible(spinner);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    /**
     * Espera o carregamento terminar: o spinner sumir e a grade de produtos aparecer.
     * false se isso não acontecer no tempo limite.
     */
    public boolean waitForProductsToLoad() {
        Log.info("Aguardando o spinner sumir e os produtos aparecerem.");
        if (!waitInvisible(spinner)) {
            return false;
        }
        try {
            waitVisible(productGrid);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
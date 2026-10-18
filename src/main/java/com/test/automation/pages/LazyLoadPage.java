package com.test.automation.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.test.automation.pages.components.HeaderComponent;
import com.test.automation.utils.Log;

/**
 * Página Dynamic Catalog > Lazy Load.
 * - Cada card mostra "Loading…" até aparecer na tela; só então exibe imagem, nome e preço.
 * - Quando o fim da lista aparece na tela, o site acrescenta mais 6 cards (lista infinita).
 * - A quantidade inicial depende do tamanho da janela: em telas grandes o site já carrega mais sozinho.
 */
public class LazyLoadPage extends BasePage {

    public static final String TITLE = "Dynamic Catalog - Lazy Load";
    private static final String URL_PATH = "dynamic-catalog-lazy-load.html";

    /** Cada carregamento acrescenta 6 cards. */
    public static final int CARDS_PER_LOAD = 6;

    /** Limite de rolagens nos loops: a lista é infinita, então o teste nunca pode ficar preso. */
    private static final int MAX_LOADS = 10;

    // Marcador invisível no fim da lista: quando aparece na tela, o site carrega mais cards.
    // Sempre existe na página, por isso @FindBy.
    @FindBy(css = "[data-test='dynamic-catalog-lazy-load-sentinel']")
    private WebElement sentinelElement;

    // Os cards são criados conforme a rolagem; por isso By
    private final By cards = By.cssSelector("[data-test='dynamic-catalog-lazy-load-container'] .dynamic_catalog_card");

    public LazyLoadPage(WebDriver driver) {
        super(driver);
    }

    public HeaderComponent header() {
        return new HeaderComponent(driver);
    }

    // Localizadores dinâmicos: o data-test de cada card tem a posição dele na lista (0, 1, 2...)
    private By card(int index) {
        return By.cssSelector("[data-test='lazy-load-item-" + index + "']");
    }

    private By cardName(int index) {
        return By.cssSelector("[data-test='lazy-load-item-" + index + "-name']");
    }

    private By cardPrice(int index) {
        return By.cssSelector("[data-test='lazy-load-item-" + index + "-price']");
    }

    private By cardPlaceholder(int index) {
        return By.cssSelector("[data-test='lazy-load-item-" + index + "-placeholder']");
    }

    // ---------- PERGUNTAS ----------

    /** true se a URL e o título forem os da página do Lazy Load (com espera). */
    public boolean isLoaded() {
        return hasPageUrl(URL_PATH) && hasTitle(TITLE);
    }

    /** Quantidade de cards na lista neste momento. */
    public int getCardCount() {
        return driver.findElements(cards).size();
    }

    /** true se o card mostra o placeholder "Loading…" (ainda não carregou). */
    public boolean isPlaceholderShown(int index) {
        return !driver.findElements(cardPlaceholder(index)).isEmpty();
    }

    /** Texto do placeholder do card. */
    public String getPlaceholderText(int index) {
        return waitVisible(cardPlaceholder(index)).getText();
    }

    /**
     * true se o card estiver inteiro abaixo da área visível da janela.
     * O JavaScript pergunta ao navegador a posição do card: se o topo dele está
     * mais abaixo que a altura da janela, ele ainda não apareceu.
     */
    public boolean isCardBelowScreen(int index) {
        WebElement cardElement = driver.findElement(card(index));
        return (Boolean) ((JavascriptExecutor) driver).executeScript(
                "return arguments[0].getBoundingClientRect().top > window.innerHeight;", cardElement);
    }

    /** Nome exibido no card (o card precisa ter carregado). */
    public String getCardName(int index) {
        return waitVisible(cardName(index)).getText();
    }

    /** Preço exibido no card (ex.: "$49.99" vira 49.99). */
    public double getCardPrice(int index) {
        return extractValue(waitVisible(cardPrice(index)).getText());
    }

    // ---------- AÇÕES ----------

    /**
     * Rola até o fim da lista e espera os novos cards aparecerem.
     * false se nenhum card novo aparecer no tempo limite.
     */
    public boolean loadMore() {
        int before = getCardCount();
        Log.info("Rolando até o fim da lista (" + before + " cards).");
        // block 'end' deixa o marcador na borda de baixo da tela: os cards novos nascem abaixo dela,
        // e o marcador sai da tela. Assim cada rolagem carrega um lote só.
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'end'});", sentinelElement);
        try {
            // Espera personalizada: "até a quantidade de cards ficar maior que antes"
            wait.until(d -> d.findElements(cards).size() > before);
            return true;
        } catch (TimeoutException e) {
            Log.warn("Nenhum card novo foi carregado após rolar até o fim.");
            return false;
        }
    }

    /** Rola quantas vezes for preciso até existir o card dessa posição. false se não conseguir. */
    public boolean loadUntilCardExists(int index) {
        for (int i = 0; i < MAX_LOADS && getCardCount() <= index; i++) {
            if (!loadMore()) {
                break;
            }
        }
        return getCardCount() > index;
    }

    /**
     * Carrega mais cards até o último ficar inteiro abaixo da tela (portanto ainda com "Loading…").
     * Devolve a posição desse card, ou -1 se não conseguir.
     * Pode precisar de mais de uma rolagem: conforme o tamanho da janela, os cards novos
     * podem nascer já visíveis.
     */
    public int loadUntilLastCardIsBelowScreen() {
        for (int i = 0; i < MAX_LOADS; i++) {
            int lastIndex = getCardCount() - 1;
            if (isCardBelowScreen(lastIndex)) {
                return lastIndex;
            }
            if (!loadMore()) {
                break;
            }
        }
        return -1;
    }

    /** Rola até o card ficar no centro da tela (o que faz ele carregar). */
    public void scrollToCard(int index) {
        Log.info("Rolando até o card " + index + ".");
        scrollTo(driver.findElement(card(index)));
    }

    /** Espera o card trocar o "Loading…" pelo nome do produto. false se não acontecer no tempo limite. */
    public boolean waitForCardToLoad(int index) {
        try {
            waitVisible(cardName(index));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
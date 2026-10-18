package com.test.automation.pages;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import com.test.automation.pages.components.HeaderComponent;
import com.test.automation.utils.Log;

/**
 * Página Dynamic Catalog > Slider.
 * - Mostra um produto por vez e troca sozinho a cada 2 segundos (depois do último, volta ao primeiro).
 * - Um ponto por produto; clicar no ponto mostra aquele produto.
 * - O ponto do produto atual tem aria-current="true".
 *
 * Como o slider continua girando enquanto o teste lê a tela, as leituras são "confirmadas":
 * o ponto ativo é lido antes e depois do nome/preço; se mudou no meio, lê de novo.
 */
public class SliderPage extends BasePage {

    public static final String TITLE = "Dynamic Catalog - Slider";
    private static final String URL_PATH = "dynamic-catalog-slider.html";

    /** Quantas vezes tentar de novo quando o slider troca no meio de uma ação. */
    private static final int MAX_ATTEMPTS = 3;

    /** Retrato do slider num instante: o ponto ativo e o produto exibido. */
    public record Slide(int index, String name, double price) {
    }

    // O site troca o conteúdo a cada 2 segundos. Com By, o elemento é buscado de novo
    // a cada leitura, o que evita erro de elemento "velho" (StaleElementReferenceException).
    private final By slideName = By.cssSelector("[data-test='dynamic-catalog-slider-item-name']");
    private final By slidePrice = By.cssSelector("[data-test='dynamic-catalog-slider-item-price']");
    private final By dots = By.cssSelector("[data-test='dynamic-catalog-slider-dots'] button");
    private final By activeDots = By.cssSelector("[data-test='dynamic-catalog-slider-dots'] button[aria-current='true']");

    public SliderPage(WebDriver driver) {
        super(driver);
    }

    public HeaderComponent header() {
        return new HeaderComponent(driver);
    }

    // A posição do ponto é o id do produto (ponto 0 = Bike Light, id 0 no enum Product)
    private By dot(int index) {
        return By.cssSelector("[data-test='dynamic-catalog-slider-dot-" + index + "']");
    }

    // ---------- PERGUNTAS ----------

    /** true se a URL e o título forem os da página do Slider (com espera). */
    public boolean isLoaded() {
        return hasPageUrl(URL_PATH) && hasTitle(TITLE);
    }

    /** Quantidade de pontos embaixo do slider. */
    public int getDotCount() {
        return driver.findElements(dots).size();
    }

    /** Quantos pontos estão marcados como atuais (o correto é sempre 1). */
    public int getActiveDotCount() {
        return driver.findElements(activeDots).size();
    }

    /** Posição do ponto ativo (0 a 5), ou -1 se nenhum estiver ativo. */
    public int getActiveDotIndex() {
        List<WebElement> dotElements = driver.findElements(dots);
        for (int i = 0; i < dotElements.size(); i++) {
            if ("true".equals(dotElements.get(i).getAttribute("aria-current"))) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Lê o produto exibido com leitura confirmada: o ponto ativo antes e depois da leitura
     * precisa ser o mesmo; se o slider trocou no meio, lê de novo.
     */
    public Slide readSlide() {
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            int before = getActiveDotIndex();
            String name = waitVisible(slideName).getText();
            double price = extractValue(waitVisible(slidePrice).getText());
            int after = getActiveDotIndex();
            if (before == after) {
                return new Slide(before, name, price);
            }
            Log.info("O slider trocou durante a leitura; lendo de novo.");
        }
        throw new IllegalStateException("O slider trocou de produto em todas as tentativas de leitura.");
    }

    // ---------- AÇÕES E ESPERAS ----------

    /**
     * Clica no ponto do produto e devolve o que ficou na tela.
     * Se o slider trocar sozinho logo depois do clique, clica de novo (até 3 vezes).
     * Quem confere se deu certo é o teste, comparando o index do Slide devolvido.
     */
    public Slide showSlide(int index) {
        Slide slide = null;
        for (int attempt = 1; attempt <= MAX_ATTEMPTS; attempt++) {
            Log.info("Clicando no ponto " + index + " do slider.");
            click(dot(index));
            slide = readSlide();
            if (slide.index() == index) {
                return slide;
            }
            Log.info("O slider avançou logo após o clique; clicando de novo.");
        }
        return slide;
    }

    /** Espera o slider sair do ponto informado (trocar sozinho). false se não trocar no tempo limite. */
    public boolean waitForSlideToChange(int fromIndex) {
        Log.info("Aguardando o slider sair do ponto " + fromIndex + ".");
        try {
            wait.until(d -> getActiveDotIndex() != fromIndex);
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
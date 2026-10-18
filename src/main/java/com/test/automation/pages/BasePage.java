package com.test.automation.pages;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.test.automation.config.ConfigReader;
import com.test.automation.utils.Log;

/**
 * Classe base de todas as páginas (Page Object).
 *
 * Guarda o que é comum a qualquer página: o driver, a espera explícita
 * (WebDriverWait),
 * a inicialização dos elementos (PageFactory) e ações genéricas (clicar,
 * digitar, ler texto, esperar).
 * Cada página herda daqui e só declara os próprios elementos e ações.
 *
 * Regra do Page Object: a página não faz Assert. Ela executa ações e responde
 * perguntas
 * (true/false, textos, listas); quem decide se o teste passou é a classe de
 * teste.
 */
public abstract class BasePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    // Título exibido no topo das páginas internas do Sauce Demo ("Products", "Your
    // Cart", ...)
    @FindBy(className = "title")
    protected WebElement pageTitleElement;

    protected BasePage(WebDriver driver) {
        this.driver = driver;
        int timeout = Integer.parseInt(ConfigReader.get("timeout.seconds", "10"));
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(timeout));
        PageFactory.initElements(driver, this); // inicializa os elementos anotados com @FindBy
        Log.info("Página carregada: " + getClass().getSimpleName());
    }

    // ---------- Esperas ----------

    /** Espera o elemento ficar visível; retorna o próprio elemento. */
    public WebElement waitVisible(WebElement element) {
        Log.info("Aguardando visibilidade do elemento: " + element);
        return wait.until(ExpectedConditions.visibilityOf(element));
    }

    /** Espera o elemento ficar visível; retorna o próprio elemento. */
    public WebElement waitVisible(By locator) {
        Log.info("Aguardando visibilidade do elemento: " + locator);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /** Espera o elemento ficar clicável; retorna o próprio elemento. */
    public WebElement waitClickable(WebElement element) {
        return wait.until(ExpectedConditions.elementToBeClickable(element));
    }

    /** Espera o elemento ficar clicável; retorna o próprio elemento. */
    public WebElement waitClickable(By locator) {
        return wait.until(ExpectedConditions.elementToBeClickable(locator));
    }

    /**
     * Espera o elemento sumir da tela (ficar invisível ou ser removido).
     * false se ele continuar visível até o tempo limite.
     * Uso típico: esperar um spinner de carregamento desaparecer.
     */
    public boolean waitInvisible(By locator) {
        Log.info("Aguardando o elemento sumir: " + locator);
        try {
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            Log.warn("O elemento continuou visível após o tempo de espera: " + locator);
            return false;
        }
    }

    // ---------- Ações genéricas ----------

    /**
     * WebElement é usado quando o elemento já foi localizado e está disponível,
     * enquanto By é usado para localizar o elemento na página. O By é uma forma de
     * especificar como encontrar um elemento (por exemplo, por ID, nome, CSS
     * selector, etc.), enquanto WebElement representa o próprio elemento na página.
     */

    /** Clica no elemento, esperando ele ficar clicável. */
    protected void click(WebElement element) {
        waitClickable(element).click();
    }

    /** Clica no elemento localizado pelo By, esperando ele ficar clicável. */
    protected void click(By locator) {
        waitClickable(locator).click();
    }

    /** Limpa o campo e digita o texto. */
    protected void type(WebElement element, String text) {
        clearElement(element);
        element.sendKeys(text);
    }

    public void clearElement(WebElement element) {
        Log.info("Limpando o campo de entrada: " + element);
        element.clear();
    }

    protected String getText(WebElement element) {
        return waitVisible(element).getText();
    }

    /** Textos de uma lista de elementos (ex.: nomes dos produtos). */
    protected List<String> getTexts(List<WebElement> elements) {
        return elements.stream().map(WebElement::getText).collect(Collectors.toList());
    }

    /**
     * true se o elemento estiver na tela, sem lançar exceção quando ele não existe.
     */
    protected boolean isDisplayed(WebElement element) {
        try {
            return element.isDisplayed();
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    // ---------- Título da página ----------

    public String getPageTitle() {
        return waitVisible(pageTitleElement).getText();
    }

    /**
     * Espera o título da página ficar igual ao esperado; false se não acontecer no
     * tempo limite.
     */
    public boolean hasTitle(String expectedTitle) {
        try {
            return wait.until(ExpectedConditions.textToBePresentInElementLocated(By.className("title"), expectedTitle));
        } catch (TimeoutException e) {
            Log.warn("Título '" + expectedTitle + "' não apareceu dentro do tempo de espera.");
            return false;
        }
    }

    /**
     * Espera a URL conter o trecho esperado; false se não acontecer no tempo
     * limite.
     */
    public boolean hasPageUrl(String expectedUrl) {
        try {
            return wait.until(ExpectedConditions.urlContains(expectedUrl));
        } catch (TimeoutException e) {
            Log.warn("URL com '" + expectedUrl + "' não apareceu dentro do tempo de espera.");
            return false;
        }
    }

    /**
     * Se houver um alerta do navegador aberto, lê o texto, clica em OK e devolve o
     * texto.
     * Se não houver alerta, devolve null. Espera no máximo 2 segundos para não
     * atrasar os testes.
     */
    public String acceptAlertIfPresent() {
        try {
            Alert alert = new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(ExpectedConditions.alertIsPresent());
            String text = alert.getText();
            alert.accept();
            Log.warn("Alerta do navegador fechado: " + text);
            return text;
        } catch (TimeoutException e) {
            return null;
        }
    }

    /**
     * Extrai o valor numérico de um texto no formato "Label: $123.45" ou "$29.99".
     */
    protected double extractValue(String label) {
        return Double.parseDouble(label.substring(label.indexOf('$') + 1));
    }

    /** Quantidade de abas/janelas abertas no navegador. */
    public int getOpenTabsCount() {
        return driver.getWindowHandles().size();
    }

    /** Recarrega a página atual (equivalente ao F5). */
    public void refreshPage() {
        Log.info("Recarregando a página.");
        driver.navigate().refresh();
    }

    /**
     * Rola a página até o elemento ficar no centro da tela (útil para o print
     * mostrar o elemento).
     */
    protected void scrollTo(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    // ---------- Verificações genéricas (vindas da primeira aula) ----------

    public void checkElementVisibility(WebElement element) {
        if (!element.isDisplayed()) {
            throw new RuntimeException("Elemento não está visível: " + element);
        }
    }

    public void checkElementText(WebElement element, String expectedText) {
        String actualText = element.getText();
        if (!actualText.equals(expectedText)) {
            throw new RuntimeException(
                    "Texto do elemento não corresponde. Esperado: " + expectedText + ", Atual: " + actualText);
        }
    }

    public void errorMessageContainsText(WebElement element, String expectedText) {
        String actualText = element.getText();
        if (!actualText.contains(expectedText)) {
            throw new RuntimeException("Mensagem de erro não contém o texto esperado. Esperado: " + expectedText
                    + ", Atual: " + actualText);
        }
    }

    public void checkElementEnabled(WebElement element) {
        if (!element.isEnabled()) {
            throw new RuntimeException("Elemento não está habilitado: " + element);
        }
    }

    public void checkElementDisabled(WebElement element) {
        if (element.isEnabled()) {
            throw new RuntimeException("Elemento não está desabilitado: " + element);
        }
    }

    public void checkElementSelected(WebElement element) {
        if (!element.isSelected()) {
            throw new RuntimeException("Elemento não está selecionado: " + element);
        }
    }

    public void checkElementNotSelected(WebElement element) {
        if (element.isSelected()) {
            throw new RuntimeException("Elemento está selecionado: " + element);
        }
    }

    public void checkElementAttribute(WebElement element, String attributeName, String expectedValue) {
        String actualValue = element.getAttribute(attributeName);
        if (!expectedValue.equals(actualValue)) {
            throw new RuntimeException(
                    "Atributo do elemento não corresponde. Esperado: " + expectedValue + ", Atual: " + actualValue);
        }
    }
}

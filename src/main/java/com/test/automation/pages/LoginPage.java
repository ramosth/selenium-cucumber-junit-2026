package com.test.automation.pages;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.test.automation.config.ConfigReader;
import com.test.automation.data.User;
import com.test.automation.utils.Log;

/**
 * Página de login (https://www.saucedemo.com/).
 */
public class LoginPage extends BasePage {

    @FindBy(id = "user-name")
    private WebElement usernameInputElement;

    @FindBy(id = "password")
    private WebElement passwordInputElement;

    @FindBy(id = "login-button")
    private WebElement loginButtonElement;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessageElement;

    // Sempre que um objeto desta página é criado, o teste precisa passar o
    // WebDriver.
    // O construtor da BasePage cuida do WebDriverWait e do PageFactory.
    public LoginPage(WebDriver driver) {
        super(driver);
    }

    /** Espera a tela de login aparecer; false se não aparecer no tempo limite. */
    public boolean isLoaded() {
        try {
            return waitVisible(loginButtonElement).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void enterUsername(String username) {
        Log.info("Inserindo nome de usuário: '" + username + "'");
        type(usernameInputElement, username);
    }

    public void enterPassword(String password) {
        Log.info("Inserindo senha.");
        type(passwordInputElement, password);
    }

    public void clickLoginButton() {
        Log.info("Clicando no botão de login.");
        click(loginButtonElement);
    }

    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    /**
     * Login com um dos usuários do Sauce Demo e devolve a página de produtos.
     * Atenção: com User.LOCKED_OUT o login é bloqueado e a página de produtos não
     * abre.
     */
    public InventoryPage loginAs(User user) {
        Log.info("Login como " + user.getUsername() + " (" + user.getDescription() + ")");
        login(user.getUsername(), ConfigReader.get("standard.password", "secret_sauce"));
        return new InventoryPage(driver);
    }

    /**
     * Atalho para o usuário padrão. Usado como pré-condição nos testes de carrinho
     * e checkout.
     */
    public InventoryPage loginAsStandardUser() {
        return loginAs(User.STANDARD);
    }

    // Login bem-sucedido = o título "Products" aparece após o login
    public boolean isLoginSuccessful() {
        try {
            return getPageTitle().equals("Products");
        } catch (TimeoutException e) {
            Log.warn("Título da página de produtos não apareceu dentro do tempo de espera.");
            return false;
        }
    }

    public String getErrorMessage() {
        return getText(errorMessageElement);
    }

    // Se a mensagem não aparecer no tempo de espera, retorna false
    public boolean isErrorMessageDisplayed() {
        try {
            return waitVisible(errorMessageElement).isDisplayed();
        } catch (TimeoutException e) {
            Log.warn("Mensagem de erro não apareceu dentro do tempo de espera.");
            return false;
        }
    }

    public String getErrorMessageText() {
        return isErrorMessageDisplayed() ? getErrorMessage() : "";
    }

    // Continuar na tela de login = o botão de login ainda está visível
    public boolean isOnLoginPage() {
        return isDisplayed(loginButtonElement);
    }
}

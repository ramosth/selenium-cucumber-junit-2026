package com.test.automation.pages;

import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.test.automation.pages.components.FooterComponent;
import com.test.automation.pages.components.HeaderComponent;
import com.test.automation.utils.Log;

/**
 * Checkout, passo 1: dados do comprador (checkout-step-one.html).
 */
public class CheckoutInformationPage extends BasePage {

    public static final String TITLE = "Checkout: Your Information";

    @FindBy(id = "first-name")
    private WebElement firstNameInputElement;

    @FindBy(id = "last-name")
    private WebElement lastNameInputElement;

    @FindBy(id = "postal-code")
    private WebElement postalCodeInputElement;

    @FindBy(css = "[data-test='continue']")
    private WebElement continueButtonElement;

    @FindBy(css = "[data-test='cancel']")
    private WebElement cancelButtonElement;

    @FindBy(css = "[data-test='error']")
    private WebElement errorMessageElement;

    public CheckoutInformationPage(WebDriver driver) {
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

    public boolean isLoaded() {
        return hasTitle(TITLE);
    }

    public void fillInformation(String firstName, String lastName, String postalCode) {
        Log.info("Preenchendo dados do checkout: '" + firstName + "' / '" + lastName + "' / '" + postalCode + "'");
        type(firstNameInputElement, firstName);
        type(lastNameInputElement, lastName);
        type(postalCodeInputElement, postalCode);
    }

    /**
     * Clica em Continue. Se os dados forem válidos, a próxima página é a de resumo
     * (Overview).
     */
    public void clickContinue() {
        click(continueButtonElement);
    }

    public CheckoutOverviewPage continueToOverview() {
        clickContinue();
        return new CheckoutOverviewPage(driver);
    }

    public CartPage cancel() {
        click(cancelButtonElement);
        return new CartPage(driver);
    }

    public boolean isErrorMessageDisplayed() {
        try {
            return waitVisible(errorMessageElement).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public String getErrorMessage() {
        return getText(errorMessageElement);
    }
}

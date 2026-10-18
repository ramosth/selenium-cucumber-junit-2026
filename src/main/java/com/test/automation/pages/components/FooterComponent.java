package com.test.automation.pages.components;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import com.test.automation.pages.BasePage;

/**
 * Componente de rodapé (footer).
 */
public class FooterComponent extends BasePage {

    @FindBy(css = "[data-test='social-x']")
    private WebElement socialXElement;

    @FindBy(css = "[data-test='social-facebook']")
    private WebElement socialFacebookElement;

    @FindBy(css = "[data-test='social-linkedin']")
    private WebElement socialLinkedinElement;

    @FindBy(css = "[data-test='footer-copy']")
    private WebElement copyRightElement;

    @FindBy(css = "[data-test='footer']")
    private WebElement footerElement;

    public FooterComponent(WebDriver driver) {
        super(driver);
    }

    /** PERGUNTAS */

    /**
     * Verificar o link do X
     */
    public boolean isVisibleX() {
        return isDisplayed(socialXElement);
    }

    /**
     * Verificar o link do Facebook
     */
    public boolean isVisibleFacebook() {
        return isDisplayed(socialFacebookElement);
    }

    /**
     * Verificar o link do LinkedIn
     */
    public boolean isVisibleLinkedIn() {
        return isDisplayed(socialLinkedinElement);
    }

    /**
     * Obter o texto do copyRight
     */
    public String getCopyRight() {
        return getText(copyRightElement);
    }

    /** Endereço do link do X (ex.: https://x.com/saucelabs). */
    public String getXLinkUrl() {
        return socialXElement.getAttribute("href");
    }

    /** Endereço do link do Facebook (ex.: https://www.facebook.com/saucelabs). */
    public String getFacebookLinkUrl() {
        return socialFacebookElement.getAttribute("href");
    }

    /**
     * Endereço do link do LinkedIn (ex.:
     * https://www.linkedin.com/company/sauce-labs/).
     */
    public String getLinkedInLinkUrl() {
        return socialLinkedinElement.getAttribute("href");
    }

    /**
     * Rola a página até o rodapé (para o print do relatório mostrar o componente).
     */
    public void scrollIntoView() {
        scrollTo(footerElement);
    }

    /** true se os 3 links de redes sociais abrem em nova aba (target="_blank"). */
    public boolean socialLinksOpenInNewTab() {
        return "_blank".equals(socialXElement.getAttribute("target"))
                && "_blank".equals(socialFacebookElement.getAttribute("target"))
                && "_blank".equals(socialLinkedinElement.getAttribute("target"));
    }

}

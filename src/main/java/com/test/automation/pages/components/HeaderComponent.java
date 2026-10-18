package com.test.automation.pages.components;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.test.automation.pages.BasePage;
import com.test.automation.pages.CartPage;
import com.test.automation.pages.InventoryPage;
import com.test.automation.pages.LazyLoadPage;
import com.test.automation.pages.LoginPage;
import com.test.automation.pages.SliderPage;
import com.test.automation.pages.SpinnerPage;
import com.test.automation.utils.Log;

/**
 * Componente de cabeçalho (header): menu lateral e ícone do carrinho.
 * Presente em todas as páginas internas; cada página o oferece pelo método
 * header().
 */
public class HeaderComponent extends BasePage {

    @FindBy(id = "react-burger-menu-btn")
    private WebElement menuButtonElement;

    @FindBy(id = "react-burger-cross-btn")
    private WebElement closeMenuButtonElement;

    @FindBy(id = "inventory_sidebar_link")
    private WebElement allItemsLinkElement;

    @FindBy(id = "dynamic_catalog_sidebar_link")
    private WebElement dynamicCatalogLinkElement;

    @FindBy(id = "about_sidebar_link")
    private WebElement aboutLinkElement;

    @FindBy(id = "logout_sidebar_link")
    private WebElement logoutLinkElement;

    @FindBy(id = "reset_sidebar_link")
    private WebElement resetAppStateLinkElement;

    @FindBy(css = "[data-test='shopping-cart-link']")
    private WebElement cartLinkElement;

    // O submenu do Dynamic Catalog só existe depois de expandido; por isso usa By
    private final By dynamicCatalogSubmenu = By.id("dynamic_catalog_submenu");
    private final By lazyLoadLink = By.id("dynamic_catalog_lazy_load_link");
    private final By spinnerLink = By.id("dynamic_catalog_spinner_link");
    private final By sliderLink = By.id("dynamic_catalog_slider_link");

    // O contador do carrinho só existe quando há itens; por isso usa By
    private final By cartBadge = By.cssSelector("[data-test='shopping-cart-badge']");

    public HeaderComponent(WebDriver driver) {
        super(driver);
    }

    // ---------- PERGUNTAS ----------

    /**
     * true se o menu lateral estiver aberto (o botão X só aparece com o menu
     * aberto).
     */
    public boolean isMenuOpen() {
        return isDisplayed(closeMenuButtonElement);
    }

    /** true se o submenu do Dynamic Catalog estiver expandido. */
    public boolean isDynamicCatalogExpanded() {
        return !driver.findElements(dynamicCatalogSubmenu).isEmpty();
    }

    /** Número exibido no ícone do carrinho (0 quando o contador não aparece). */
    public int getCartBadgeCount() {
        List<WebElement> badge = driver.findElements(cartBadge);
        return badge.isEmpty() ? 0 : Integer.parseInt(badge.get(0).getText());
    }

    /**
     * Endereço do link 'About' (abre o menu, se estiver fechado, para ler o link).
     */
    public String getAboutLinkUrl() {
        openMenu();
        return aboutLinkElement.getAttribute("href");
    }

    /**
     * Atributo target do link 'About'.
     * Vazio = abre na mesma aba; "_blank" = abre em nova aba.
     */
    public String getAboutLinkTarget() {
        openMenu();
        String target = aboutLinkElement.getAttribute("target");
        return target == null ? "" : target;
    }

    // ---------- AÇÕES: menu ----------

    /** Abre o menu lateral. Se já estiver aberto, não faz nada. */
    public void openMenu() {
        if (isMenuOpen()) {
            return;
        }
        Log.info("Abrindo o menu lateral.");
        click(menuButtonElement);
        waitVisible(closeMenuButtonElement); // espera o menu terminar de abrir
    }

    /**
     * Fecha o menu lateral e espera ele sumir. Se já estiver fechado, não faz nada.
     */
    public void closeMenu() {
        if (!isMenuOpen()) {
            return;
        }
        Log.info("Fechando o menu lateral.");
        click(closeMenuButtonElement);
        wait.until(ExpectedConditions.invisibilityOf(closeMenuButtonElement));
    }

    /** Abre o menu, clica em 'All Items' e vai para a página de produtos. */
    public InventoryPage goToAllItems() {
        openMenu();
        Log.info("Clicando em All Items.");
        click(allItemsLinkElement);
        return new InventoryPage(driver);
    }

    /**
     * Abre o menu e expande o submenu do Dynamic Catalog (não navega, só mostra as
     * opções).
     */
    public void expandDynamicCatalog() {
        openMenu();
        if (!isDynamicCatalogExpanded()) {
            Log.info("Expandindo o Dynamic Catalog.");
            click(dynamicCatalogLinkElement);
        }
        waitVisible(lazyLoadLink); // espera as opções aparecerem
    }

    /** Vai para Dynamic Catalog > Lazy Load. */
    public LazyLoadPage goToLazyLoad() {
        expandDynamicCatalog();
        Log.info("Clicando em Lazy Load.");
        click(lazyLoadLink);
        return new LazyLoadPage(driver);
    }

    /** Vai para Dynamic Catalog > Spinner. */
    public SpinnerPage goToSpinner() {
        expandDynamicCatalog();
        Log.info("Clicando em Spinner.");
        click(spinnerLink);
        return new SpinnerPage(driver);
    }

    /** Vai para Dynamic Catalog > Slider. */
    public SliderPage goToSlider() {
        expandDynamicCatalog();
        Log.info("Clicando em Slider.");
        click(sliderLink);
        return new SliderPage(driver);
    }

    /**
     * Abre o menu e clica em 'About'. Leva para o site da Sauce Labs (o teste
     * confere a URL).
     */
    public void goToAbout() {
        openMenu();
        Log.info("Clicando em About.");
        click(aboutLinkElement);
    }

    /** Abre o menu, clica em 'Logout' e volta para a página de login. */
    public LoginPage logout() {
        openMenu();
        Log.info("Fazendo logout.");
        click(logoutLinkElement);
        return new LoginPage(driver);
    }

    /**
     * Abre o menu e clica em 'Reset App State' (limpa o carrinho). O menu continua
     * aberto.
     */
    public void resetAppState() {
        openMenu();
        Log.info("Clicando em Reset App State.");
        click(resetAppStateLinkElement);
    }

    // ---------- AÇÕES: carrinho ----------

    /** Abre a página do carrinho. */
    public CartPage openCart() {
        Log.info("Abrindo o carrinho.");
        click(cartLinkElement);
        return new CartPage(driver);
    }
}
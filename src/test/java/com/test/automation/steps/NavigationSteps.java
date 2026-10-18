package com.test.automation.steps;

import java.time.Year;

import org.junit.jupiter.api.Assertions;

import com.test.automation.config.ConfigReader;
import com.test.automation.context.TestContext;
import com.test.automation.pages.components.FooterComponent;
import com.test.automation.pages.components.HeaderComponent;
import com.test.automation.utils.Log;

import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

/**
 * Passos das partes presentes em todas as páginas internas:
 * menu lateral (HeaderComponent) e rodapé (FooterComponent).
 *
 * Verificações com o Assertions do JUnit 5. Atenção à ordem no assertEquals:
 * primeiro o ESPERADO, depois o valor ATUAL (o contrário do TestNG).
 */
public class NavigationSteps {

    private final TestContext context;

    // Quantidade de abas antes do clique em About (para conferir que nenhuma nova foi aberta)
    private int abasAntesDoAbout;

    public NavigationSteps(TestContext context) {
        this.context = context;
    }

    private HeaderComponent header() {
        return new HeaderComponent(context.getDriver());
    }

    private FooterComponent footer() {
        return new FooterComponent(context.getDriver());
    }

    // ---------- Menu lateral: ações ----------

    @Quando("abro o menu lateral")
    public void abroOMenuLateral() {
        header().openMenu();
    }

    @Quando("fecho o menu lateral")
    public void fechoOMenuLateral() {
        header().closeMenu();
    }

    @Quando("clico em All Items no menu lateral")
    public void clicoEmAllItems() {
        header().goToAllItems();
    }

    @Quando("clico em About no menu lateral")
    public void clicoEmAbout() {
        HeaderComponent header = header();
        abasAntesDoAbout = header.getOpenTabsCount();
        header.goToAbout();
    }

    @Quando("clico em Reset App State no menu lateral")
    public void clicoEmResetAppState() {
        header().resetAppState(); // o menu continua aberto depois do reset
    }

    @Quando("acesso a página {string} direto pela URL")
    public void acessoAPaginaDiretoPelaUrl(String pagina) {
        String url = ConfigReader.get("base.url", "https://www.saucedemo.com/") + pagina;
        Log.info("Acessando direto pela URL: " + url);
        context.getDriver().get(url);
    }

    // ---------- Menu lateral: validações ----------

    @Entao("o menu lateral deve estar aberto")
    public void oMenuLateralDeveEstarAberto() {
        Assertions.assertTrue(header().isMenuOpen(), "O menu deveria estar aberto.");
    }

    @Entao("o menu lateral deve estar fechado")
    public void oMenuLateralDeveEstarFechado() {
        Assertions.assertFalse(header().isMenuOpen(), "O menu deveria estar fechado.");
    }

    @Entao("o link About deve apontar para {string}")
    public void oLinkAboutDeveApontarPara(String urlEsperada) {
        // JUnit: (esperado, atual, mensagem)
        Assertions.assertEquals(urlEsperada, header().getAboutLinkUrl(),
                "O link 'About' aponta para outro endereço.");
    }

    // target vazio = mesma aba; "_blank" = nova aba
    @Entao("o link About deve estar configurado para abrir na mesma aba")
    public void oLinkAboutDeveAbrirNaMesmaAba() {
        Assertions.assertEquals("", header().getAboutLinkTarget(),
                "O link 'About' está configurado para abrir em outra aba.");
    }

    @Entao("devo estar no site {string}")
    public void devoEstarNoSite(String trechoDaUrl) {
        Assertions.assertTrue(header().hasPageUrl(trechoDaUrl), "A URL atual não contém '" + trechoDaUrl + "'.");
    }

    @Entao("nenhuma aba nova deve ter sido aberta")
    public void nenhumaAbaNovaDeveTerSidoAberta() {
        // Esperado: a quantidade de abas de ANTES do clique
        Assertions.assertEquals(abasAntesDoAbout, header().getOpenTabsCount(), "O clique abriu outra aba.");
    }

    // ---------- Rodapé ----------

    @Quando("rolo até o rodapé")
    public void roloAteORodape() {
        footer().scrollIntoView(); // assim o print do relatório mostra o rodapé
    }

    @Entao("o link do X deve apontar para {string}")
    public void oLinkDoXDeveApontarPara(String urlEsperada) {
        Assertions.assertTrue(footer().isVisibleX(), "O link do X não está visível.");
        Assertions.assertEquals(urlEsperada, footer().getXLinkUrl(), "Link do X diferente do esperado.");
    }

    @Entao("o link do Facebook deve apontar para {string}")
    public void oLinkDoFacebookDeveApontarPara(String urlEsperada) {
        Assertions.assertTrue(footer().isVisibleFacebook(), "O link do Facebook não está visível.");
        Assertions.assertEquals(urlEsperada, footer().getFacebookLinkUrl(),
                "Link do Facebook diferente do esperado.");
    }

    @Entao("o link do LinkedIn deve apontar para {string}")
    public void oLinkDoLinkedInDeveApontarPara(String urlEsperada) {
        Assertions.assertTrue(footer().isVisibleLinkedIn(), "O link do LinkedIn não está visível.");
        Assertions.assertEquals(urlEsperada, footer().getLinkedInLinkUrl(),
                "Link do LinkedIn diferente do esperado.");
    }

    // Links externos abrem em nova aba, para o usuário não sair da loja
    @Entao("os links das redes sociais devem abrir em nova aba")
    public void osLinksDasRedesSociaisDevemAbrirEmNovaAba() {
        Assertions.assertTrue(footer().socialLinksOpenInNewTab(),
                "Os links das redes sociais deveriam abrir em nova aba.");
    }

    @Entao("o copyright deve ser {string}")
    public void oCopyrightDeveSer(String textoEsperado) {
        // "[ano atual]" no .feature vira o ano de hoje: o cenário não quebra na virada do ano
        String esperado = textoEsperado.replace("[ano atual]", String.valueOf(Year.now().getValue()));
        Assertions.assertEquals(esperado, footer().getCopyRight(), "Texto de copyright diferente do esperado.");
    }
}
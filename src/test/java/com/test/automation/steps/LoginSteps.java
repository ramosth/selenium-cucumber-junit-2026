package com.test.automation.steps;

import org.testng.Assert;

import com.test.automation.context.TestContext;
import com.test.automation.data.User;
import com.test.automation.pages.InventoryPage;
import com.test.automation.pages.LoginPage;
import com.test.automation.pages.components.HeaderComponent;
import com.test.automation.utils.Log;

import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.E;
import io.cucumber.java.pt.Entao;
import io.cucumber.java.pt.Quando;

/**
 * Passos de login e logout.
 *
 * O texto de cada anotação precisa ser IGUAL à frase do .feature (sem a palavra
 * Dado/Quando/Então/E). {string} captura o texto entre aspas e entrega como parâmetro.
 *
 * A palavra-chave não importa para o Cucumber: uma frase escrita com "E" no .feature
 * encontra um método anotado com @Entao, e vice-versa.
 *
 * Os passos são globais e independentes: cada um cria a LoginPage a partir do driver,
 * então funcionam em qualquer feature (ex.: a mensagem de erro depois de um logout).
 */
public class LoginSteps {

    private final TestContext context;

    public LoginSteps(TestContext context) {
        this.context = context;
    }

    // ---------- Pré-condições ----------

    @Dado("que estou na página de login")
    public void queEstouNaPaginaDeLogin() {
        Assert.assertTrue(new LoginPage(context.getDriver()).isLoaded(), "A tela de login não foi exibida.");
    }

    /** Atalho usado como pré-condição pelas outras features (carrinho, checkout...). */
    @Dado("que estou logado com o usuário padrão")
    public void queEstouLogadoComOUsuarioPadrao() {
        InventoryPage inventoryPage = new LoginPage(context.getDriver()).loginAsStandardUser();
        Assert.assertTrue(inventoryPage.isLoaded(), "A página de produtos não foi exibida após o login.");
    }

    /** Login com qualquer usuário do Sauce Demo. Ex.: "que estou logado com o usuário PROBLEM" */
    @Dado("que estou logado com o usuário {usuario}")
    public void queEstouLogadoComOUsuario(User usuario) {
        InventoryPage inventoryPage = new LoginPage(context.getDriver()).loginAs(usuario);
        Assert.assertTrue(inventoryPage.isLoaded(), "A página de produtos não foi exibida após o login.");
    }

    // ---------- Ações ----------

    @Quando("faço login com o usuário {string} e a senha {string}")
    public void facoLoginComOUsuarioEASenha(String usuario, String senha) {
        Log.info("Login com o usuário: '" + usuario + "'");
        new LoginPage(context.getDriver()).login(usuario, senha);
    }

    @Quando("faço logout")
    public void facoLogout() {
        LoginPage loginPage = new HeaderComponent(context.getDriver()).logout();
        Assert.assertTrue(loginPage.isLoaded(), "Após o logout, deveria voltar para a tela de login.");
    }

    // ---------- Validações: login com sucesso ----------

    @Entao("devo ver a página de produtos")
    public void devoVerAPaginaDeProdutos() {
        Assert.assertTrue(new LoginPage(context.getDriver()).isLoginSuccessful(),
                "Era esperado entrar no sistema, mas a tela 'Products' não foi exibida.");
    }

    // ---------- Validações: login com erro ----------

    @Entao("devo ver a mensagem de erro {string}")
    public void devoVerAMensagemDeErro(String mensagemEsperada) {
        LoginPage loginPage = new LoginPage(context.getDriver());
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(),
                "Era esperada uma mensagem de erro, mas nenhuma foi exibida.");
        Assert.assertEquals(loginPage.getErrorMessage(), mensagemEsperada,
                "A mensagem de erro exibida é diferente da esperada.");
    }

    @E("devo continuar na página de login")
    public void devoContinuarNaPaginaDeLogin() {
        Assert.assertTrue(new LoginPage(context.getDriver()).isOnLoginPage(),
                "Era esperado continuar na tela de login.");
    }
}
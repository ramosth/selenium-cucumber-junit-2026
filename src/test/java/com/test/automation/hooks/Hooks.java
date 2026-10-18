package com.test.automation.hooks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.test.automation.config.ConfigReader;
import com.test.automation.context.TestContext;
import com.test.automation.driver.DriverFactory;
import com.test.automation.reports.ExtentReportManager;
import com.test.automation.utils.Log;
import com.test.automation.utils.ScreenshotUtils;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;

/**
 * Ciclo de vida da execução (substitui o BaseTest).
 *
 *   @BeforeAll -> cria o Extent Report (uma vez por execução)
 *   @Before    -> registra o cenário no Extent e abre o navegador (antes de cada cenário)
 *   @After     -> tira o print, registra o resultado e fecha o navegador
 *   @AfterAll  -> grava o Extent Report em disco
 *
 * Prints: sempre salvos em screenshots/<STATUS>. Nas falhas, também são anexados
 * ao Extent e ao relatório do Cucumber.
 */
public class Hooks {

    private final TestContext context;

    public Hooks(TestContext context) {
        this.context = context;
    }

    @BeforeAll
    public static void setupReport() {
        Log.info("Configurando o Extent Report antes da execução dos cenários.");
        ExtentReportManager.getReportInstance();
    }

    @AfterAll
    public static void tearDownReport() {
        ExtentReportManager.getReportInstance().flush();
        Log.info("Relatório Extent gerado em: " + ExtentReportManager.reportFilePath);
    }

    @Before
    public void setUp(Scenario scenario) {
        Log.info("Iniciando o cenário: " + scenario.getName());

        // Registro do cenário no Extent; as tags (@login...) viram categorias no relatório
        ExtentTest test = ExtentReportManager.createTest(scenario.getName());
        for (String tag : scenario.getSourceTagNames()) {
            test.assignCategory(tag.replace("@", ""));
        }
        context.setExtentTest(test);

        WebDriver driver = DriverFactory.createDriver();
        String baseUrl = ConfigReader.get("base.url", "https://www.saucedemo.com/");
        Log.info("Navegando para: " + baseUrl);
        driver.get(baseUrl);
        context.setDriver(driver);
    }

    @After
    public void tearDown(Scenario scenario) {
        ExtentTest test = context.getExtentTest();
        WebDriver driver = context.getDriver();
        Log.info("Cenário finalizado: " + scenario.getName() + " | Status: " + scenario.getStatus());

        // Se o navegador nem abriu, não há print para tirar nem navegador para fechar
        if (driver == null) {
            test.fail("O navegador não foi aberto. Veja o log da execução para o motivo.");
            return;
        }

        String screenshotName = buildScreenshotName(scenario);

        switch (scenario.getStatus()) {
            case PASSED -> {
                ScreenshotUtils.capture(driver, screenshotName, "SUCCESS");
                test.pass("Cenário concluído com sucesso.");
            }
            case FAILED -> {
                Log.error("O cenário falhou: " + scenario.getName());
                String path = ScreenshotUtils.capture(driver, screenshotName, "FAILURE");
                registerWithScreenshot(scenario, test, Status.FAIL, path,
                        "Falha no cenário. O passo que quebrou e a mensagem do erro estão no relatório do Cucumber.");
            }
            default -> {
                // SKIPPED, UNDEFINED (frase sem step), PENDING...
                Log.warn("O cenário não foi executado por completo: " + scenario.getStatus());
                String path = ScreenshotUtils.capture(driver, screenshotName, "SKIP");
                registerWithScreenshot(scenario, test, Status.SKIP, path,
                        "Cenário não executado por completo (status: " + scenario.getStatus() + ").");
            }
        }

        Log.info("Fechando o navegador.");
        driver.quit();
    }

    /** Registra o resultado no Extent com o print e anexa o mesmo print ao relatório do Cucumber. */
    private void registerWithScreenshot(Scenario scenario, ExtentTest test, Status status, String path,
            String message) {
        if (path == null) {
            test.log(status, message);
            return;
        }
        test.log(status, message, MediaEntityBuilder.createScreenCaptureFromPath(path).build());
        try {
            scenario.attach(Files.readAllBytes(Paths.get(path)), "image/png", "Captura de tela");
        } catch (IOException e) {
            Log.warn("Não foi possível anexar o print ao relatório do Cucumber: " + e.getMessage());
        }
    }

    /**
     * Nome do print: <arquivo da feature>_<ID do cenário>.
     * Exemplo: login.feature + "CT01 - Login com usuário e senha válidos" -> login_CT01
     * Se o cenário não tiver ID no começo do nome, usa o nome inteiro (sem espaços e acentos).
     */
    private String buildScreenshotName(Scenario scenario) {
        String uri = scenario.getUri().toString();
        String feature = uri.substring(uri.lastIndexOf('/') + 1).replace(".feature", "");

        String id = scenario.getName().split(" - ")[0].trim();
        if (id.matches("[A-Z]+\\d+")) {
            return feature + "_" + id;
        }
        return feature + "_" + scenario.getName().replaceAll("[^a-zA-Z0-9]+", "_");
    }
}
package com.test.automation.driver;

import java.util.HashMap;
import java.util.Map;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import com.test.automation.config.ConfigReader;
import com.test.automation.utils.DownloadUtils;
import com.test.automation.utils.Log;

/**
 * Cria o navegador (WebDriver) de acordo com o config.properties.
 *
 * Centralizar a criação aqui permite trocar de navegador ou rodar sem interface (headless)
 * sem mexer em nenhum teste:
 *   browser=chrome | firefox | edge
 *   headless=true  | false
 * Ou numa execução específica: mvn test -Dbrowser=firefox -Dheadless=true
 *
 * Os downloads vão direto para a pasta de downloads do projeto (DownloadUtils), sem perguntar.
 */
public class DriverFactory {

    private DriverFactory() {
    }

    public static WebDriver createDriver() {
        String browser = ConfigReader.get("browser", "chrome").toLowerCase();
        boolean headless = ConfigReader.getBoolean("headless", false);
        Log.info("Iniciando o navegador: " + browser + (headless ? " (headless)" : ""));

        WebDriver driver = switch (browser) {
            case "chrome" -> new ChromeDriver(chromeOptions(headless));
            case "firefox" -> new FirefoxDriver(firefoxOptions(headless));
            case "edge" -> new EdgeDriver(edgeOptions(headless));
            default -> throw new IllegalArgumentException(
                    "Navegador não suportado: '" + browser + "'. Use chrome, firefox ou edge.");
        };

        if (!headless) {
            driver.manage().window().maximize();
        }
        return driver;
    }

    /** Preferências comuns ao Chrome e ao Edge (os dois usam o mesmo motor, o Chromium). */
    private static Map<String, Object> chromiumPrefs() {
        Map<String, Object> prefs = new HashMap<>();
        // Desliga o gerenciador de senhas e o aviso de "senha vazada".
        // Sem isso, depois do login no Sauce Demo pode aparecer um pop-up que atrapalha os cliques.
        prefs.put("credentials_enable_service", false);
        prefs.put("profile.password_manager_enabled", false);
        prefs.put("profile.password_manager_leak_detection", false);
        // Downloads: pasta do projeto, sem perguntar onde salvar
        prefs.put("download.default_directory", DownloadUtils.getDownloadDir());
        prefs.put("download.prompt_for_download", false);
        prefs.put("download.directory_upgrade", true);
        prefs.put("plugins.always_open_pdf_externally", true); // baixa o PDF em vez de abrir no navegador
        return prefs;
    }

    private static ChromeOptions chromeOptions(boolean headless) {
        ChromeOptions options = new ChromeOptions();
        options.setExperimentalOption("prefs", chromiumPrefs());
        if (headless) {
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        return options;
    }

    private static FirefoxOptions firefoxOptions(boolean headless) {
        FirefoxOptions options = new FirefoxOptions();
        // Downloads: pasta do projeto, sem perguntar onde salvar
        options.addPreference("browser.download.folderList", 2); // 2 = usar a pasta definida abaixo
        options.addPreference("browser.download.dir", DownloadUtils.getDownloadDir());
        options.addPreference("browser.download.useDownloadDir", true);
        options.addPreference("browser.helperApps.neverAsk.saveToDisk", "application/pdf");
        options.addPreference("pdfjs.disabled", true); // baixa o PDF em vez de abrir no visualizador
        if (headless) {
            options.addArguments("-headless");
        }
        return options;
    }

    private static EdgeOptions edgeOptions(boolean headless) {
        EdgeOptions options = new EdgeOptions();
        options.setExperimentalOption("prefs", chromiumPrefs());
        if (headless) {
            options.addArguments("--headless=new", "--window-size=1920,1080");
        }
        return options;
    }
}
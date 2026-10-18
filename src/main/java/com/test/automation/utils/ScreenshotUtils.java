package com.test.automation.utils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

/**
 * Captura de tela dos testes.
 * Salva em screenshots/<STATUS>/<nome>_<data-hora>.png (STATUS = SUCCESS, FAILURE ou SKIP).
 */
public class ScreenshotUtils {

    private ScreenshotUtils() {
    }

    /** Tira o print e devolve o caminho do arquivo (ou null se não conseguir). */
    public static String capture(WebDriver driver, String screenshotName, String status) {
        try {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            File screenshotDir = new File(System.getProperty("user.dir") + "/screenshots/" + status);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs(); // cria a pasta se ainda não existir
            }
            File destination = new File(screenshotDir, screenshotName + "_" + timestamp + ".png");
            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            FileUtils.copyFile(source, destination);
            Log.info("Captura de tela salva em: " + destination.getAbsolutePath());
            return destination.getAbsolutePath();
        } catch (Exception e) {
            Log.logException("Não foi possível salvar a captura de tela.", e);
            return null;
        }
    }
}

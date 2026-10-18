package com.test.automation.reports;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.test.automation.config.ConfigReader;

/**
 * Cria e guarda a instância única do Extent Report da execução.
 * O relatório é salvo em reports/ExtentReport_<data-hora>.html.
 */
public class ExtentReportManager {

    private static ExtentReports extent;

    // Caminho do relatório desta execução (usado pelo e-mail para anexar o arquivo)
    public static String reportFilePath;

    private ExtentReportManager() {
    }

    public static ExtentReports getReportInstance() {
        if (extent == null) {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            reportFilePath = "reports/ExtentReport_" + timestamp + ".html";

            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(reportFilePath);
            sparkReporter.config().setDocumentTitle("Automation Test Report");
            sparkReporter.config().setReportName("Test Execution Report");

            extent = new ExtentReports();
            extent.attachReporter(sparkReporter);
            // Informações do ambiente exibidas no painel do relatório
            extent.setSystemInfo("Aplicação", ConfigReader.get("base.url", "https://www.saucedemo.com/"));
            extent.setSystemInfo("Navegador", ConfigReader.get("browser", "chrome"));
        }
        return extent;
    }

    public static ExtentTest createTest(String testName) {
        return getReportInstance().createTest(testName);
    }
}

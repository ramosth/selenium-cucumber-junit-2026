package com.test.automation.context;

import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.ExtentTest;

/**
 * "Mochila" compartilhada durante UM cenário.
 *
 * O PicoContainer cria um TestContext novo no início de cada cenário e entrega
 * a MESMA instância para todas as classes que pedirem por ele no construtor
 * (Hooks, LoginSteps, CartSteps...). Assim todo mundo usa o mesmo navegador
 * e o mesmo registro do relatório, sem herança.
 *
 * Quando o cenário termina, o objeto é descartado; o próximo cenário começa do zero.
 */
public class TestContext {

    private WebDriver driver;
    private ExtentTest extentTest;

    public WebDriver getDriver() {
        return driver;
    }

    public void setDriver(WebDriver driver) {
        this.driver = driver;
    }

    /** Registro do cenário atual no Extent Report (criado no @Before dos Hooks). */
    public ExtentTest getExtentTest() {
        return extentTest;
    }

    public void setExtentTest(ExtentTest extentTest) {
        this.extentTest = extentTest;
    }
}
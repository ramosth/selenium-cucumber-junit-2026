package com.test.automation.runners;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

/**
 * Ponto de entrada da execução: substitui as suítes XML do projeto TestNG.
 *
 * O Surefire encontra esta classe pelo nome (padrão "Test*") e o TestNG a
 * executa.
 * A classe-mãe AbstractTestNGCucumberTests transforma cada cenário do .feature
 * em um teste do TestNG.
 *
 * Para rodar só uma parte dos cenários, use tags:
 * mvn test "-Dcucumber.filter.tags=@login"
 */
@CucumberOptions(
        // Onde estão os arquivos .feature
        features = "src/test/resources/features",
        // Pacotes onde o Cucumber procura os Hooks e os Steps
        glue = { "com.test.automation.hooks", "com.test.automation.steps" },
        // "pretty" mostra cada passo no console; "html" gera o relatório do Cucumber
        plugin = { "pretty", "html:reports/cucumber-report.html" },
        // Console sem códigos de cor (fica legível no terminal do Windows e no Jenkins)
        monochrome = true)
public class TestRunner extends AbstractTestNGCucumberTests {
    // Vazia de propósito: a classe-mãe já faz todo o trabalho
}